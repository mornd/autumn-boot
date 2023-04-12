package com.mornd.process.service;

/**
 * @author: mornd
 * @dateTime: 2023/4/12 - 23:54
 * 微信公众号通知
 */
public interface WechatMessageService {

    /**
     * 给待审批人员推送消息
     * @param processId 流程 id
     * @param userId 推送消息的用户 id
     * @param taskId 任务 id
     */
    void pushPendingMessage(Long processId, String userId, String taskId);

    /**
     * 审批完成后给提交人推送消息
     * @param processId 流程id
     * @param userId 推送消息的用户 id
     * @param status 状态(通过或拒绝)
     */
    void pushProcessedMessage(Long processId, String userId, String status);
}
