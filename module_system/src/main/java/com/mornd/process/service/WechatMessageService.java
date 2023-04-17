package com.mornd.process.service;

import com.mornd.process.entity.Process;

/**
 * @author: mornd
 * @dateTime: 2023/4/12 - 23:54
 * 微信公众号通知
 */
public interface WechatMessageService {

    /**
     * 给待审批人员推送消息
     * @param process 流程对象
     * @param userId 推送消息的用户 id
     * @param taskId 任务 id
     */
    void pushPendingMessage(Process process, String userId, String taskId);

    /**
     * 审批完成后给提交人推送消息
     * @param process 流程对象
     * @param userId 推送消息的用户 id
     * @param status 状态(通过或拒绝)
     * @param reason 拒绝理由
     */
    void pushProcessedMessage(Process process, String userId, Integer status, String reason);
}
