package com.mornd.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.process.entity.vo.ApprovalVo;
import com.mornd.process.entity.Process;
import com.mornd.process.entity.vo.ProcessFormVo;
import com.mornd.process.entity.vo.ProcessVo;
import org.activiti.engine.repository.Deployment;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * @author: mornd
 * @dateTime: 2023/3/29 - 11:28
 */
public interface ProcessService extends IService<Process> {

    /**
     * 查询流程实例列表
     * @param vo
     * @return
     */
    IPage<ProcessVo> pageList(ProcessVo vo);

    /**
     * 获取流程文件位置
     * @return
     */
    String getProcessFilePath() throws FileNotFoundException;

    /**
     * 发布流程
     * @param processTemplateId
     * @return
     */
    boolean publish(Long processTemplateId);

    /**
     * 流程部署
     * @param filename
     * @return
     */
    Deployment deployByZip(String filename);

    /**
     * 启动流程
     * @param vo
     */
    void startup(ProcessFormVo vo);

    /**
     * 流程审批
     * @param vo
     */
    void approve(ApprovalVo vo);

    /**
     * 查看某个审批详情
     * @param id
     * @return
     */
    Map<String, Object> show(Long id);

    /**
     * 查询我的待审批列表
     * @param process
     * @return
     */
    IPage<ProcessVo> findPending(Process process);

    /**
     * 查询我已处理的审批列表
     * @param process
     * @return
     */
    IPage<ProcessVo> findProcessed(Process process);

    /**
     * 查询我发起的审批列表
     * @param process
     * @return
     */
    IPage<Process> findStarted(Process process);
}
