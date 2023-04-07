package com.mornd.mail.config;

import com.mornd.mail.constants.MailFailureConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: mornd
 * @dateTime: 2023/2/8 - 22:01
 * 定义错误交换机
 */

@Configuration
public class ErrorMessageConfig {

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(MailFailureConstants.EXCHANGE, true, false);
    }

    @Bean
    public Queue errorQueue() {
        return QueueBuilder
                .durable(MailFailureConstants.QUEUE)
                // 绑定死信交换机
//                .deadLetterExchange("")
//                .deadLetterRoutingKey("")
                // 声明惰性队列(队列消息存入磁盘，而不是放在内存中)
                //.lazy()
                // 声明仲裁队列(用于集群配置)
                //.quorum()
                .build();
    }

    /**
     * 声明队列，交换机的绑定关系
     * @return
     */
    @Bean
    public Binding errorBinding() {
        return BindingBuilder.bind(errorQueue())
                .to(directExchange())
                .with(MailFailureConstants.ROUTING_KEY);
    }

    /**
     * 定义消息 retry 后还是失败的处理方式
     * @param rabbitTemplate
     * @return
     */
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        // 重新发布到 error 交换机
        return new RepublishMessageRecoverer(rabbitTemplate,
                MailFailureConstants.EXCHANGE, MailFailureConstants.ROUTING_KEY);
    }

    /**
     * 消费者声明 amqp 的消息序列方式
     * 消费者和接收者都要配置
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
