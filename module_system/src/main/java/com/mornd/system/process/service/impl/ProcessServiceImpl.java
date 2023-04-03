package com.mornd.system.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.AutumnException;
import com.mornd.system.process.constant.ProcessConst;
import com.mornd.system.process.entity.Process;
import com.mornd.system.process.entity.ProcessTemplate;
import com.mornd.system.process.entity.vo.ProcessFormVo;
import com.mornd.system.process.entity.vo.ProcessVo;
import com.mornd.system.process.mapper.ProcessMapper;
import com.mornd.system.process.service.ProcessRecordService;
import com.mornd.system.process.service.ProcessService;
import com.mornd.system.process.service.ProcessTemplateService;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.AutumnUUID;
import com.mornd.system.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

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

    private final ProcessTemplateService processTemplateService;

    private final UserService userService;

    private final ProcessRecordService processRecordService;

    @Override
    public IPage<ProcessVo> pageList(ProcessVo vo) {
        IPage<ProcessVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        baseMapper.pageList(page, vo);
        return page;
    }

    /**
     * 流程部署
     * @param filename 文件名
     * @return
     */
    @Override
    public Deployment deployByZip(String filename) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(ProcessConst.PROCESS_DIR_NAME + File.separator + filename);

        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deploy = repositoryService
                .createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();

        log.info("流程部署成功");
        log.info("流程部署id：{}", deploy.getId());
        log.info("流程部署名称：{}", deploy.getName());
        return deploy;
    }

    /**
     * 启动流程实例
     * @param vo
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
        // 流程状态
        process.setStatus(Process.Status.PROGRESSING.getCode());
        //  用户id
        process.setUserId(loginUser.getId());
        // 表单 json 数据
        process.setFormValues(vo.getFormValues());
        // 标题
        process.setTitle(loginUser.getRealName() + "发起" + processTemplate.getName() + "申请");
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
        List<String> names = new ArrayList<>();
        for (Task task : taskList) {
            // 获取用户的登录名称
            String assignee = task.getAssignee();
            // 根据登录名称获取真实姓名
            LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
            qw.eq(SysUser::getLoginName, assignee);
            qw.select(SysUser::getRealName);
            SysUser sysUser = userService.getOne(qw);
            names.add(sysUser.getRealName());

            //todo 消息推送
        }

        // 更新 process 表
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待" + StringUtils.join(names.toArray(), ",") + "审批");
        boolean update = this.updateById(process);

        // 添加审批记录
        processRecordService.insert(process.getId(), process.getStatus(), "发起申请");
    }

    private List<Task> getCurrentTaskList(String id) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(id).list();
        return taskList;
    }

    /**
     * 用户查询自己的待办流程
     * @param process
     * @return
     */
    @Override
    public IPage<ProcessVo> findPending(Process process) {
        SysUser loginUser = SecurityUtil.getLoginUser();
        TaskQuery taskQuery =
                // 根据当前的用户登录名查询
                taskService.createTaskQuery().taskAssignee(loginUser.getLoginName())
                // 通过创建时间降序
                .orderByTaskCreateTime().desc();

        // 自定义分页，Long 转 int
        int pageNo = (int) ((process.getPageNo() - 1) * process.getPageSize());
        int pageSize = (int) process.getPageSize().longValue();
        List<Task> tasks = taskQuery.listPage(pageNo, pageSize);

        // 返回结果
        List<ProcessVo> processVoList = new ArrayList<>();

        for (Task task : tasks) {
            // 获取流程实例id
            String instanceId = task.getProcessInstanceId();
            // 通过流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(instanceId).singleResult();

            // 通过流程实例获取业务key，就是 process 表的 id
            String businessKey = processInstance.getBusinessKey();
            if(businessKey == null) {
                continue;
            }
            // 查询数据库
            Process dbProcess = super.getById(Long.valueOf(businessKey));

            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(dbProcess, processVo);
            processVo.setTaskId(task.getId());

            processVoList.add(processVo);
        }

        // 封装返回 mp 的 IPage 对象
        // 待办总数
        long total = taskQuery.count();
        IPage<ProcessVo> page = new Page<>(process.getPageNo(), process.getPageSize(), total);
        page.setRecords(processVoList);
        return page;
    }
}
