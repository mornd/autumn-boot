package com.mornd.system.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.process.entity.Process;
import com.mornd.system.process.entity.vo.ApprovalVo;
import com.mornd.system.process.entity.vo.ProcessFormVo;
import com.mornd.system.process.entity.vo.ProcessVo;
import org.activiti.engine.repository.Deployment;

import java.util.Map;

/**
 * @author: mornd
 * @dateTime: 2023/3/29 - 11:28
 */
public interface ProcessService extends IService<Process> {

    IPage<ProcessVo> pageList(ProcessVo vo);

    /**
     * 流程部署
     * @param filename
     * @return
     */
    Deployment deployByZip(String filename);

    void startup(ProcessFormVo vo);

    void approve(ApprovalVo vo);

    Map<String, Object> show(Long id);

    IPage<Process> findPending(Process process);

    IPage<Process> findProcessed(Process process);


    IPage<Process> findStarted(Process process);
}
