package com.mornd.mail.listener;

import com.mornd.mail.entity.AmqpMail;
import com.mornd.mail.constants.MailFailureConstants;
import com.mornd.mail.entity.MailLog;
import com.mornd.mail.mapper.MailLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: mornd
 * @dateTime: 2023/2/9 - 18:53
 */

@Slf4j
@Component
public class ErrorReceiver {

    @Resource
    private MailLogMapper mailLogMapper;


    /**
     * retry 重试后还是失败处理
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MailFailureConstants.QUEUE, durable = "true"),
            exchange = @Exchange(name = MailFailureConstants.EXCHANGE, type = ExchangeTypes.DIRECT, durable = "true", autoDelete = "false"),
            key = MailFailureConstants.ROUTING_KEY
    ))
    public void handle(Message message) {
        try {
            MessageHeaders headers = message.getHeaders();
            // 消息 id
            String msgId = (String) headers.get("spring_returned_message_correlation");
            // 异常类型
            String exceptionMessage = (String) headers.get("x-exception-message");
            // 异常消息
            String exceptionStacktrace = headers.get("x-exception-stacktrace").toString();
            // 交换机
            String exchange = (String) headers.get("x-original-exchange");
            // 路由键
            String routingKey = (String) headers.get("x-original-routingKey");
            // 消息体
            AmqpMail amqpMail = (AmqpMail) message.getPayload();

            // 更新数据库
            MailLog mailLog = new MailLog(msgId, MailLog.MailLogStatus.CONSUME_ERROR.ordinal(),
                    "消息消费时发生异常：" + exceptionMessage + " " +  exceptionStacktrace);

            mailLogMapper.updateById(mailLog);
        } catch (Exception e) {
            //todo 还是失败时处理
            log.error("errorExchange处理消息发生异常：", e);
        }
    }
}
