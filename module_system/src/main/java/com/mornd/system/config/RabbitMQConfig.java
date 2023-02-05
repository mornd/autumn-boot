package com.mornd.system.config;

import com.mornd.system.MailConstants;
import com.mornd.system.entity.po.MailLog;
import com.mornd.system.mapper.MailLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2023/2/4 - 13:12
 * rabbit MQ 配置类
 */

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Resource
    private MailLogMapper mailLogMapper;

    /**
     * 连接工厂
     */
    @Resource
    public CachingConnectionFactory cachingConnectionFactory;

    /**
     * 配置手动确认消息
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        /**
         * 设置消息确认回调，确认消息是否到达 broker
         * data：消息唯一标识
         * ack：确认结果 true：投递成功，false：投递失败
         * cause：失败原因
         */
        rabbitTemplate.setConfirmCallback((data, ack, cause) -> {
            String msgId = data.getId();
            if(ack) {
                // 更改数据库状态为投递成功
                MailLog mailLog = new MailLog();
                mailLog.setMsgId(msgId);
                mailLog.setStatus(MailConstants.SUCCESS);
                mailLog.setUpdateTime(LocalDateTime.now());
                mailLogMapper.updateById(mailLog);
                log.info("消息id：{}投递成功", msgId);
            } else {
                //todo 失败处理
                log.error("消息id：{}投递失败：{}", msgId, cause);
            }
        });
        //rabbitTemplate.setReturnCallback((Message message, int replyCode, String replyText, String exchange, String routingKey) -> {});

        rabbitTemplate.setReturnsCallback((returnedMessage) -> {
            // 消息主题
            Message message = returnedMessage.getMessage();
            // 响应码
            int replyCode = returnedMessage.getReplyCode();
            // 描述
            String replyText = returnedMessage.getReplyText();
            // 交换机
            String exchange = returnedMessage.getExchange();
            // 路由键
            String routingKey = returnedMessage.getRoutingKey();

            //todo
            log.error("消息{}投递到queue时失败", message.getBody());
        });

        return rabbitTemplate;
    }

    /**
     * 队列
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(MailConstants.QUEUE_NAME);
    }

    /**
     * 交换机
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(MailConstants.EXCHANGE_NAME);
    }

    /**
     * 绑定关系
     * @return
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue())
                .to(directExchange())
                .with(MailConstants.ROUTING_KEY_NAME);
    }
}
