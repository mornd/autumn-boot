package com.mornd.system.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mornd.system.entity.AmqpMail;
import com.mornd.system.entity.MailLog;
import com.mornd.system.mapper.MailLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author: mornd
 * @dateTime: 2023/2/4 - 13:12
 * rabbit MQ 配置类
 */

@Slf4j
@Configuration
public class RabbitMQConfig implements ApplicationContextAware {

    @Resource
    private MailLogMapper mailLogMapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        // returnCallback 消息到达交换机但是没到队列 (比如：没有匹配的路由键)
        rabbitTemplate.setReturnsCallback((returnedMessage -> {
            Message message = returnedMessage.getMessage();

            // 消息id
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            String msgId = (String) headers.get("spring_returned_message_correlation");

            // 消息
            byte[] body = message.getBody();
            String msgBody = new String(body, StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            AmqpMail amqpMail1 = null;
            try {
                // 这里我是知道 mq 使用 jackson 转换的，所以这里才用 objectMapper 对象
                amqpMail1 = objectMapper.readValue(msgBody, AmqpMail.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String exchange = returnedMessage.getExchange();
            int replyCode = returnedMessage.getReplyCode();
            String replyText = returnedMessage.getReplyText();
            String routingKey = returnedMessage.getRoutingKey();
            log.error("消息投递失败，消息：{}，回执码：{}，失败消息：{}，交换机：{}，路由键：{}",
                    message, replyCode, replyText, exchange, routingKey);

            // 更新数据库
            MailLog mailLog = new MailLog(msgId, MailLog.MailLogStatus.TO_QUEUE_ERROR.ordinal(),
                    "消息投递到队列失败：" + replyText);
            mailLogMapper.updateById(mailLog);
        }));

        /**
         * 这里是消息投递到交换机的回执
         * 这里是全局处理，或者在 convertAndSend 时局部处理
         * 若这里处理了，局部可不用处理，否则局部和这里的全局回调方法都会执行
         */
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                if(ack) {
//                    log.info("消息id：{}投递到交换机成功", correlationData.getId());
//            } else {
//                log.error("消息id：{}投递到交换机失败：{}", correlationData.getId(), cause);
//                }
//            }
//        });

        // 发布者声明 amqp 的消息序列方式
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }
}
