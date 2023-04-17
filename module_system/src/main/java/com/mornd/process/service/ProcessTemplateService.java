package com.mornd.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.process.entity.ProcessTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: mornd
 * @dateTime: 2023/3/26 - 16:09
 */
public interface ProcessTemplateService extends IService<ProcessTemplate> {
    IPage<ProcessTemplate> pageList(ProcessTemplate template);

    void insertAndUploadProcessDefinition(ProcessTemplate processTemplate, MultipartFile file) throws IOException;

    void updateAndUploadProcessDefinition(ProcessTemplate processTemplate, MultipartFile file) throws IOException;

    boolean checkName(String name, Long id);

    boolean checkFileName(String filename, Long id);
}
