package com.mornd.mail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2023/2/9 - 21:26
 */


@Data
@TableName("mail_log")
public class MailLog {
    public MailLog() {
    }

    /**
     * 失败更新时构建
     * @param msgId
     * @param status
     * @param errorMessage
     */
    public MailLog(String msgId, Integer status, String errorMessage) {
        this.msgId = msgId;
        this.status = status;
        this.errorMessage = errorMessage;
        this.updateTime = LocalDateTime.now();
    }

    @TableId(type = IdType.NONE)
    private String msgId;
    private String userId;
    /**
     * 状态(0：消息投递中，1：投递成功，2：发送到交换机失败，3：发送到队列失败，4：消费消息时发生异常失败)
     */
    private Integer status;
    private String exchange;
    private String routingKey;
    private String errorMessage;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;


    /**
     * 错误状态枚举
     */
    public enum MailLogStatus {
        /**
         * 投递中
         */
        DELIVERING,
        /**
         * 投递成功
         */
        SUCCESS,
        /**
         * 发送到交换机错误
         */
        TO_EXCHANGE_ERROR,
        /**
         * 发送到队列错误
         */
        TO_QUEUE_ERROR,
        /**
         * 消费时出现异常
         */
        CONSUME_ERROR
    }
}

