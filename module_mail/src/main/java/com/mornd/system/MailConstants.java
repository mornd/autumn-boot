package com.mornd.system;

/**
 * @author: mornd
 * @dateTime: 2023/2/1 - 21:52
 * 用户邮件通知常量类
 */
public interface MailConstants {
    /**
     * 消息投递中
     */
    Integer DELIVERING = 0;
    /**
     * 消息投递成功
     */
    Integer SUCCESS = 1;
    /**
     * 消息投递失败
     */
    Integer FAILURE = 2;
    /**
     *  消息发送失败后最大重发次数
     */
    Integer MAX_TRY_COUNT = 3;
    /**
     * 消息发送失败后间隔多次时间再次发送 单位：分钟
     */
    Integer MSG_TIMEOUT = 1;

    /**
     * 队列名称
     */
    String QUEUE_NAME = "mail.userWelcome.queue";
    /**
     * 交换机
     */
    String EXCHANGE_NAME = "mail.userWelcome.exchange";
    /**
     * 路由键
     */
    String ROUTING_KEY_NAME = "mail.userWelcome.routingKey";

    /**
     * 存储 缓存 的邮件记录 key
     */
    String CACHE_KEY = "mail_userWelcome_log";
}
