package com.mornd.system.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.process.entity.Process;
import com.mornd.system.process.entity.vo.ProcessVo;
import org.activiti.engine.repository.Deployment;

/**
 * @author: mornd
 * @dateTime: 2023/3/29 - 11:28
 */
public interface ProcessService extends IService<Process> {
    /**
     * 流程部署
     * @param filename
     * @return
     */
    Deployment deployByZip(String filename);

    IPage<ProcessVo> pageList(ProcessVo vo);
}
