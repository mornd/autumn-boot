package com.mornd.mail.constants;

/**
 * @author: mornd
 * @dateTime: 2023/2/1 - 21:52
 * 用户邮件通知常量类
 */
public abstract class MailConstants {

    /**
     * 队列名称
     */
    public static final String QUEUE_NAME = "mail.userWelcome.queue";
    /**
     * 交换机
     */
    public static final String EXCHANGE_NAME = "mail.userWelcome.exchange";
    /**
     * 路由键
     */
    public static final String ROUTING_KEY_NAME = "mail.userWelcome.routingKey";
}
