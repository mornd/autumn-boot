package com.mornd.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.process.mapper.ProcessTemplateMapper;
import com.mornd.process.service.ProcessService;
import com.mornd.process.service.ProcessTemplateService;
import com.mornd.process.service.ProcessTypeService;
import com.mornd.system.exception.AutumnException;
import com.mornd.process.entity.ProcessTemplate;
import com.mornd.process.entity.ProcessType;
import com.mornd.system.utils.SecurityUtil;
import org.activiti.engine.repository.Deployment;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.mornd.process.constant.ProcessConst.PROCESS_FILE_SUFFIX;
import static com.mornd.process.entity.ProcessTemplate.Status.PUBLISHED;
import static com.mornd.process.entity.ProcessTemplate.Status.UNPUBLISHED;

/**
 * @author: mornd
 * @dateTime: 2023/3/26 - 16:09
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessTemplateServiceImpl
        extends ServiceImpl<ProcessTemplateMapper, ProcessTemplate>
        implements ProcessTemplateService {

    @Resource
    private ProcessTypeService processTypeService;

    @Resource
    @Lazy
    private ProcessService processService;

    /**
     * 列表查询
     * @param template
     * @return
     */
    @Override
    public IPage<ProcessTemplate> pageList(ProcessTemplate template) {
        Page<ProcessTemplate> page = new Page<>(template.getPageNo(), template.getPageSize());
        LambdaQueryWrapper<ProcessTemplate> qw = Wrappers.lambdaQuery(ProcessTemplate.class);
        qw.like(StringUtils.hasText(template.getName()),
                ProcessTemplate::getName, template.getName());
        qw.eq(template.getProcessTypeId() != null,
                ProcessTemplate::getProcessTypeId, template.getProcessTypeId());
        baseMapper.selectPage(page, qw);

        page.getRecords().stream().filter(i -> i.getProcessTypeId() != null)
                .forEach(i -> {
                    // 查询审批类型名称
                    ProcessType type = processTypeService.getById(i.getProcessTypeId());
                    if(type != null) {
                        i.setProcessTypeName(type.getName());
                    }
                });

        page.getRecords().forEach(i -> {
            if(i.getStatus().equals(UNPUBLISHED.ordinal())) {
                i.setStateStr(UNPUBLISHED.getDesc());
            } else if (i.getStatus().equals(PUBLISHED.ordinal())) {
                i.setStateStr(PUBLISHED.getDesc());
            }
        });

        return page;
    }

    @Transactional(rollbackFor = Exception.class) // 保证 IOException 也能回滚事务
    @Override
    public void insertAndUploadProcessDefinition(ProcessTemplate processTemplate,
                                                                MultipartFile file) throws IOException {
        if(checkName(processTemplate.getName(), processTemplate.getId())) {
            throw new AutumnException("模板名称已重复，请更换");
        }
        String filename = file.getOriginalFilename();
        if(checkFileName(filename, processTemplate.getId())) {
            throw new AutumnException("流程文件名称已重复，请更换");
        }
        processTemplate.setStatus(UNPUBLISHED.ordinal());
        // 入库做记录
        processTemplate.setCreateId(SecurityUtil.getLoginUserId());
        processTemplate.setCreateTime(LocalDateTime.now());

        // 获取流程定义key
        processTemplate.setProcessDefinitionKey(filename.substring(0, filename.lastIndexOf(PROCESS_FILE_SUFFIX)));
        // 获取流程定义文件的路径
        processTemplate.setProcessDefinitionFileName(filename);

        boolean save = super.save(processTemplate);
        if(save) {
            uploadProcessDefinition(file);
        } else {
            throw new AutumnException("提交失败");
        }
    }

    @Override
    public void updateAndUploadProcessDefinition(ProcessTemplate entity,
                                                 MultipartFile file) throws IOException {
        ProcessTemplate old = super.getById(entity.getId());
        if(!old.getStatus().equals(UNPUBLISHED.ordinal())) {
            throw new AutumnException("该流程已发布，不可修改");
        }
        if(checkName(entity.getName(), entity.getId())) {
            throw new AutumnException("模板名称已重复，请更换");
        }

        LambdaUpdateWrapper<ProcessTemplate> uw = Wrappers.lambdaUpdate();
        uw.eq(ProcessTemplate::getId, entity.getId());
        uw.set(ProcessTemplate::getName, entity.getName());
        uw.set(ProcessTemplate::getIconUrl, entity.getIconUrl());
        uw.set(ProcessTemplate::getProcessTypeId, entity.getProcessTypeId());
        uw.set(ProcessTemplate::getFormProps, entity.getFormProps());
        uw.set(ProcessTemplate::getFormOptions, entity.getFormOptions());
        uw.set(ProcessTemplate::getDescription, entity.getDescription());
        uw.set(ProcessTemplate::getUpdateId, SecurityUtil.getLoginUserId());
        uw.set(ProcessTemplate::getUpdateTime, LocalDateTime.now());

        String filename = null;
        if(entity.getUpdateFile()) {
            filename = file.getOriginalFilename();
            if(checkFileName(filename, entity.getId())) {
                throw new AutumnException("流程文件名称已重复，请更换");
            }

            // 获取流程定义key
            uw.set(ProcessTemplate::getProcessDefinitionKey,
                    filename.substring(0, filename.lastIndexOf(PROCESS_FILE_SUFFIX)));
            // 获取流程定义文件的路径
            uw.set(ProcessTemplate::getProcessDefinitionFileName, filename);
        }

        boolean save = super.update(uw);
        if(save) {
            if(entity.getUpdateFile()) {
                // 删除之前的流程定义文件
                deleteResourceFile(old.getProcessDefinitionFileName());
                uploadProcessDefinition(file);
            }
        } else {
            throw new AutumnException("提交失败");
        }
    }

    /**
     * 上传流程定义文件
     * @param file 文件对象
     * @return 流程定义的路径和key
     * @throws IOException 文件拷贝异常
     */
    private void uploadProcessDefinition(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        // 获取 target/classes/process 目录位置
        String resourcePath = processService.getProcessFilePath();
        File processFile = new File(resourcePath);
        if(!processFile.exists()) {
            processFile.mkdirs();
        }
        // 流程定义文件
        File zipFile = new File(processFile, filename);
        // 复制
        file.transferTo(zipFile);
    }

    /**
     * 删除 target 目录下的流程定义文件
     * @param filename
     * @return
     */
    private boolean deleteResourceFile(String filename) {
        File file = null;
        try {
            file = new File(processService.getProcessFilePath(), filename);
        } catch (FileNotFoundException e) {
            log.error("流程定义文件不存在", e);
            return true;
        }
        if(file.exists() && file.isFile()) {
            return file.delete();
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        ProcessTemplate entity = super.getById(id);
        boolean row = super.removeById(id);
        if(row) {
            return deleteResourceFile(entity.getProcessDefinitionFileName());
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean publish(Long id) {
        ProcessTemplate entity = super.getById(id);
        if(!entity.getStatus().equals(UNPUBLISHED.ordinal())) {
            throw new RuntimeException("该数据状态错误");
        }
        entity.setStatus(PUBLISHED.ordinal());
        boolean row = super.updateById(entity);
        if(row) {
            if(StringUtils.hasText(entity.getProcessDefinitionFileName())) {
                // 发布流程
                Deployment deployment =
                        processService.deployByZip(entity.getProcessDefinitionFileName());
                return Objects.nonNull(deployment);
            }
        }
        return false;
    }

    @Override
    public boolean checkName(String name, Long id) {
        LambdaQueryWrapper<ProcessTemplate> qw = Wrappers.lambdaQuery();
        qw.eq(ProcessTemplate::getName, name);
        if(id != null) {
            // 修改
            qw.ne(ProcessTemplate::getId, id);
        }
        // 添加
        return baseMapper.selectCount(qw) > 0;
    }

    @Override
    public boolean checkFileName(String filename, Long id) {
        LambdaQueryWrapper<ProcessTemplate> qw = Wrappers.lambdaQuery();
        qw.eq(ProcessTemplate::getProcessDefinitionFileName, filename);
        if(id != null) {
            // 修改
            qw.ne(ProcessTemplate::getId, id);
        }
        // 添加
        return baseMapper.selectCount(qw) > 0;
    }
}
