package com.mornd.process.service.impl;

import cn.hutool.core.collection.IterUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.process.entity.vo.ApprovalVo;
import com.mornd.process.entity.vo.ProcessVo;
import com.mornd.process.mapper.ProcessMapper;
import com.mornd.process.service.ProcessService;
import com.mornd.process.service.ProcessTemplateService;
import com.mornd.process.service.WechatMessageService;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.AutumnException;
import com.mornd.process.entity.Process;
import com.mornd.process.entity.ProcessRecord;
import com.mornd.process.entity.ProcessTemplate;
import com.mornd.process.entity.vo.ProcessFormVo;
import com.mornd.process.service.ProcessRecordService;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.AutumnUUID;
import com.mornd.system.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipInputStream;

import static com.mornd.process.constant.ProcessConst.PROCESS_DIR_NAME;
import static com.mornd.process.constant.ProcessConst.PROCESS_PATH;
import static com.mornd.process.entity.Process.ApproveStatus.AGREE;
import static com.mornd.process.entity.Process.ApproveStatus.REJECT;
import static com.mornd.process.entity.Process.Status.COMPLETED;
import static com.mornd.process.entity.Process.Status.PROGRESSING;
import static com.mornd.process.entity.ProcessTemplate.Status.PUBLISHED;
import static com.mornd.process.entity.ProcessTemplate.Status.UNPUBLISHED;

/**
 * @author: mornd
 * @dateTime: 2023/3/29 - 11:28
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProcessServiceImpl
        extends ServiceImpl<ProcessMapper, Process>
        implements ProcessService {

    private final RepositoryService repositoryService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final HistoryService historyService;

    private final ProcessTemplateService processTemplateService;

    private final UserService userService;

    private final ProcessRecordService processRecordService;

    private final WechatMessageService wechatMessageService;

    @Override
    public IPage<ProcessVo> pageList(ProcessVo vo) {
        IPage<ProcessVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        baseMapper.pageList(page, vo);
        for (ProcessVo record : page.getRecords()) {
            String auditorId = record.getCurrentAuditorId();
            if(StringUtils.hasText(auditorId)) {
                // 获取当前审批人集合，使用逗号拼接成字符串
                StringBuilder realNameStr = new StringBuilder();
                StringBuilder infoStr = new StringBuilder();
                String[] ids = auditorId.split(",");
                for (int i = 0; i < ids.length; i++) {
                    if(StringUtils.hasText(ids[i])) {
                        SysUser sysUser = userService.getById(ids[i]);
                        realNameStr.append(sysUser.getRealName());
                        infoStr.append(sysUser.getRealName() + "(" + sysUser.getLoginName() + ")");
                        if(i < ids.length - 1) {
                            realNameStr.append(",");
                            infoStr.append(",");
                        }
                    }
                }
                record.setCurrentAuditorRealName(realNameStr.toString());
                record.setCurrentAuditorInfo(infoStr.toString());
            }
        }
        return page;
    }

    /**
     * 获取 target/classes/process 目录下存放流程文件夹的位置
     * E:\xxx\autumn_boot\module_system\target\classes\process
     * @return /mornd/xxx/file:/mornd/autumn/autumn-server.jar!/BOOT-INF/classes!/process
     * @throws FileNotFoundException
     */
    @Override
    public String getProcessFilePath() throws FileNotFoundException {
        return new File(ResourceUtils.getURL("classpath:")
                .getPath(), PROCESS_DIR_NAME)
                .getAbsolutePath();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean publish(Long processTemplateId) {
        ProcessTemplate entity = processTemplateService.getById(processTemplateId);
        if(!entity.getStatus().equals(UNPUBLISHED.ordinal())) {
            throw new RuntimeException("该数据状态错误");
        }
        entity.setStatus(PUBLISHED.ordinal());
        boolean row = processTemplateService.updateById(entity);
        if(row) {
            if(StringUtils.hasText(entity.getProcessDefinitionFileName())) {
                // 发布流程
                Deployment deployment =
                        this.deployByZip(entity.getProcessDefinitionFileName());
                return Objects.nonNull(deployment);
            }
        }
        return false;
    }

    /**
     * 流程部署
     * @param filename 文件名
     * @return
     */
    @Override
    public Deployment deployByZip(String filename) {
        Deployment deploy = null;
        File processFile = null;
        try {
            processFile = new File(getProcessFilePath(), filename);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            throw new AutumnException("流程文件不存在");
        }

        try (InputStream inputStream = Files.newInputStream(processFile.toPath());) {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            deploy = repositoryService.createDeployment()
                    .addZipInputStream(zipInputStream)
                    .deploy();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AutumnException("流程发布失败");
        }

        // 这种方式在windows可用，linux环境会读取不到文件
//        InputStream inputStream = this.getClass().getClassLoader() // 必须使用 ClassLoader 对象调用，如果用 Class 对象调用则返回 null
//                .getResourceAsStream(ProcessConst.PROCESS_DIR_NAME + File.separator + filename);
//        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
//        Deployment deploy = repositoryService
//                .createDeployment()
//                .addZipInputStream(zipInputStream)
//                //.name("")
//                .deploy();

        log.info("流程部署成功，id为：{}", deploy.getId());
        //log.info("流程部署名称：{}", deploy.getName());
        // 可查看表 act_re_deployment 记录
        return deploy;
    }

    /**
     * 启动流程实例
     * @param vo
     * 可能会报的错：
     * ActivitiObjectNotFoundException: no processes deployed with key 'simpleqingjia'
     *      你的流程文件xml中process的id属性值与zip文件名不一致
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void startup(ProcessFormVo vo) {
        SysUser loginUser = SecurityUtil.getLoginUser();

        // 根据模板id获取数据
        ProcessTemplate processTemplate = processTemplateService.getById(vo.getProcessTemplateId());

        if(processTemplate == null) {
            throw new AutumnException("流程模板不存在");
        }
        // 插入 process 表数据
        Process process = new Process();
        // 流程编码
        process.setProcessCode(AutumnUUID.fastSimpleUUID());
        // 标题
        process.setTitle(loginUser.getRealName() + "发起《" + processTemplate.getName() + "》申请");
        // 流程状态
        process.setStatus(PROGRESSING.getCode());
        //  用户id
        process.setUserId(loginUser.getId());
        // 表单 json 数据
        process.setFormValues(vo.getFormValues());
        process.setProcessTemplateId(processTemplate.getId());
        process.setProcessTypeId(processTemplate.getProcessTypeId());
        process.setCreateId(loginUser.getId());
        process.setCreateTime(LocalDateTime.now());

        boolean save = super.save(process);

        // 业务id
        String businessKey = String.valueOf(process.getId());

        // 流程定义 key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();

        // 将表单数据放入流程实例中
        String formValues = vo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");

        // 流程变量
        Map<String,Object> variables = new HashMap<>();
        variables.put("data", new HashMap<>(formData));

        /**
         * 启动流程实例
         * 参数一：流程定义key
         * 参数二：业务key
         * 参数三：流程参数 form 表单，转为 map
         */
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);

        // 查询下一个审批人
        List<Task> taskList = this.getCurrentTaskList(processInstance.getId());

        StringBuilder auditIds = new StringBuilder();
        StringBuilder auditNames = new StringBuilder();
        for (Task task : taskList) {
            // 获取用户的登录名称
            String assignee = task.getAssignee();
            // 根据登录名称获取真实姓名
            LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
            qw.eq(SysUser::getLoginName, assignee);
            qw.select(SysUser::getId, SysUser::getRealName, SysUser::getPhone);
            SysUser sysUser = userService.getOne(qw);
            if(sysUser != null) {
                auditIds.append(sysUser.getId()).append(",");
                auditNames.append(sysUser.getRealName()).append(",");
            }

            //todo 推送消息给审批人
            wechatMessageService.pushPendingMessage(process, sysUser.getId(), task.getId());
        }

        // 更新 process 表
        process.setProcessInstanceId(processInstance.getId());
        if(auditIds.length() > 0) {
            process.setCurrentAuditorId(auditIds.substring(0, auditIds.length() - 1));
            process.setDescription("等待" + auditNames.substring(0, auditNames.length() - 1) + "审批");
        }

        boolean update = this.updateById(process);

        // 添加审批记录
        processRecordService.insert(process.getId(), process.getStatus(), "发起申请");
    }

    private List<Task> getCurrentTaskList(String id) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(id).list();
        return taskList;
    }

    /**
     * 开始审批 1：同意 -1：驳回
     * @param vo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approve(ApprovalVo vo) {
        // 获取任务id
        String taskId = vo.getTaskId();

        String desc = null;
        // 获取流程变量
        Map<String, Object> variables = taskService.getVariables(taskId);

        if(AGREE.getCode().equals(vo.getStatus())) {
            desc = "通过";
            // 可继续添加流程变量
            Map<String,Object> map = new HashMap<String, Object>();
            // 完成
            taskService.complete(taskId, map);
        } else if(REJECT.getCode().equals(vo.getStatus())) {
            desc = "驳回";
            this.endTask(taskId);
        } else {
            throw new AutumnException("流程审批状态错误");
        }
        // 添加流程记录
        //todo 1：审批中，-1：驳回
        processRecordService.insert(vo.getProcessId(), vo.getStatus(), desc);

        // 查询下一个审批人
        Process process = super.getById(vo.getProcessId());
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if(IterUtil.isNotEmpty(taskList)) {

            StringBuilder auditIds = new StringBuilder();
            StringBuilder auditNames = new StringBuilder();
            for (Task task : taskList) {
                String assignee = task.getAssignee();
                // 根据登录名称获取用户的真实姓名
                LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
                qw.select(SysUser::getId, SysUser::getRealName, SysUser::getPhone);
                qw.eq(SysUser::getLoginName, assignee);
                SysUser sysUser = userService.getOne(qw);
                if(sysUser != null) {
                    auditIds.append(sysUser.getId() + ",");
                    auditNames.append(sysUser.getRealName() + ",");
                }

                //todo 推送消息给审批人
                wechatMessageService.pushPendingMessage(process, sysUser.getId(), task.getId());
            }
            if(auditIds.length() > 0) {
                process.setCurrentAuditorId(auditIds.substring(0, auditIds.length() - 1));
                process.setDescription("等待" + auditNames.substring(0, auditNames.length() - 1) + "审批");
            }
        } else {
            // taskList 为空表示没有下一个节点流程结束了，此时再更改流程状态
            if(AGREE.getCode().equals(vo.getStatus())) {
                process.setStatus(COMPLETED.getCode());
                process.setDescription("审批完成(通过)");
            } else {
                process.setStatus(REJECT.getCode());
                process.setDescription("审批完成(驳回)");
                // 拒绝理由
                process.setReason(vo.getReason());
            }
            // 处理时间
            process.setHandleTime(LocalDateTime.now());
            // 将当前审批人置空
            //process.setCurrentAuditorId("");

            //todo 流程结束，推送消息给流程发起人，告知结果
            wechatMessageService.pushProcessedMessage(process, process.getUserId(), vo.getStatus(), vo.getReason());
        }
        super.updateById(process);
    }

    @Override
    public Map<String, Object> show(Long id) {
        Process process = super.getById(id);
        if(process == null) {
            throw new AutumnException("流程为空");
        }

        // 通过流程id查询流程记录
        LambdaQueryWrapper<ProcessRecord> qw = Wrappers.lambdaQuery(ProcessRecord.class);
        qw.eq(ProcessRecord::getProcessId, process.getId());
        List<ProcessRecord> processRecordList = processRecordService.list(qw);

        // 通过流程模板id获取详情
        ProcessTemplate processTemplate =
                processTemplateService.getById(process.getProcessTemplateId());

        // 判断当前用户是否可以审批(比较task集合中是否存在当前登录的用户)
        String loginUsername = SecurityUtil.getLoginUsername();
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        boolean isApprove = taskList.stream()
                .anyMatch(i -> loginUsername.equals(i.getAssignee()));

        // 返回结果
        Map<String,Object> result = new HashMap<String, Object>(4);
        result.put("process", process);
        result.put("processRecordList", processRecordList);
        result.put("processTemplate", processTemplate);
        result.put("isApprove", isApprove);
        return result;
    }

    /**
     * 用户查询当前待处理流程
     * @param process
     * @return
     */
    @Override
    public IPage<ProcessVo> findPending(Process process) {
        TaskQuery taskQuery =
                // 根据当前的用户登录名查询
                taskService.createTaskQuery().taskAssignee(SecurityUtil.getLoginUsername())
                // 通过创建时间降序
                .orderByTaskCreateTime().desc();

        // 查询待办总数
        long count = taskQuery.count();
        if(count <= 0) {
            return new Page<>();
        }

        // 自定义分页，Long 转 int
        int pageNo = (int) ((process.getPageNo() - 1) * process.getPageSize());
        int pageSize = (int) process.getPageSize().longValue();
        List<Task> tasks = taskQuery.listPage(pageNo, pageSize);

        // 返回结果
        List<ProcessVo> processList = new ArrayList<>();

        for (Task task : tasks) {
            // 获取流程实例id
            String instanceId = task.getProcessInstanceId();
            // 通过流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(instanceId).singleResult();

            // 通过流程实例获取业务key，就是 process 表的 id
            String businessKey = processInstance.getBusinessKey();
            Process dbProcess = super.getById(Long.valueOf(businessKey));
            // 添加任务id，用于审批同意或拒绝时携带
            dbProcess.setTaskId(task.getId());

            ProcessVo vo = new ProcessVo();
            BeanUtils.copyProperties(dbProcess, vo);
            // 获取申请人
            SysUser user = userService.getById(dbProcess.getUserId());
            if(user != null) {
                vo.setUserName(user.getLoginName());
                vo.setUserRealName(user.getRealName());
            }
            processList.add(vo);
        }

        // 封装返回 mp 的 IPage 对象
        IPage<ProcessVo> page = new Page<>(process.getPageNo(), process.getPageSize(), count);
        page.setRecords(processList);
        return page;
    }

    /**
     * 用户查询当前已处理流程
     * @param process
     * @return
     */
    @Override
    public IPage<ProcessVo> findProcessed(Process process) {
        // 封装查询条件
        HistoricTaskInstanceQuery instanceQuery =
                historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(SecurityUtil.getLoginUsername())
                .finished()
                //.orderByTaskCreateTime()
                .orderByHistoricTaskInstanceEndTime()
                .desc();

        long count = instanceQuery.count();
        if(count <= 0) {
            return new Page<>();
        }

        // 自定义分页，Long 转 int
        int pageNo = (int) ((process.getPageNo() - 1) * process.getPageSize());
        int pageSize = (int) process.getPageSize().longValue();
        List<HistoricTaskInstance> historicList = instanceQuery.listPage(pageNo, pageSize);

        List<ProcessVo> processList = new ArrayList<>();
        for (HistoricTaskInstance historic : historicList) {
            // 获取流程实例id
            String processInstanceId = historic.getProcessInstanceId();
            // 根据流程实例id查询流程
            LambdaQueryWrapper<Process> qw = Wrappers.lambdaQuery(Process.class);
            qw.eq(Process::getProcessInstanceId, processInstanceId);
            Process dbProcess = super.getOne(qw);

            ProcessVo vo = new ProcessVo();
            BeanUtils.copyProperties(dbProcess, vo);
            // 获取申请人
            SysUser user = userService.getById(vo.getUserId());
            if(user != null) {
                vo.setUserName(user.getLoginName());
                vo.setUserRealName(user.getRealName());
            }
            processList.add(vo);
        }

        // 封装返回 mp 的 IPage 对象
        IPage<ProcessVo> page = new Page<>(process.getPageNo(), process.getPageSize(), count);
        page.setRecords(processList);
        return page;
    }

    /**
     * 用户查询当前已发起流程
     * @param process
     * @return
     */
    @Override
    public IPage<Process> findStarted(Process process) {
        IPage<Process> page = new Page<>(process.getPageNo(), process.getPageSize());
        LambdaQueryWrapper<Process> qw = Wrappers.lambdaQuery(Process.class);
        qw.eq(Process::getUserId, SecurityUtil.getLoginUserId());
        qw.orderByDesc(Process::getId);
        super.page(page, qw);
        return page;
    }

    /**
     * 手动更改流向来结束流程
     * @param taskId
     */
    private void endTask(String taskId) {
        // 根据任务id获取task任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取流程定义模型
        BpmnModel bpmnModel =
                repositoryService.getBpmnModel(task.getProcessDefinitionId());

        // 获取结束流向节点
        List<EndEvent> endEventList =
                bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);

        if(IterUtil.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = endEventList.get(0);

        // 获取当前流向节点
        FlowNode currentFlowNode =
                (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        // 临时保存当前活动的原始反向
        List<SequenceFlow> originalOutgoingFlows = new ArrayList<>();
        originalOutgoingFlows.addAll(currentFlowNode.getOutgoingFlows());

        // 清理当前流动反向
        currentFlowNode.getOutgoingFlows().clear();

        // 创建新流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlow");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);

        // 将当前节点指向新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);

        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        // 完成任务
        taskService.complete(taskId);
    }
}
