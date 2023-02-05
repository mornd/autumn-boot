package com.mornd.system;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author: mornd
 * @dateTime: 2023/2/1 - 19:29
 * 邮件接收
 *
 * 消息投递可靠性问题 -> 1、消息落库（对消息状态进行打标）2、消息延迟投递（做二次确认，回调检查）
 * 消费幂等性 一条消息投递多次 -> 入库记录投递状态
 *
 *
 */

@Slf4j
@Component
public class MailReceiver {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private MailProperties mailProperties;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TemplateEngine templateEngine;

    @RabbitListener(queues = MailConstants.QUEUE_NAME)
    public void handle(Message message, Channel channel) {
        MessageHeaders headers = message.getHeaders();
        String msgId = (String) headers.get("spring_returned_message_correlation");
        // 消息序号
        long tag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
        HashOperations hashOperations = redisTemplate.opsForHash();

        try {
            if(hashOperations.entries(MailConstants.CACHE_KEY).containsKey(msgId)) {
                log.info("消息：{}已被消费", msgId);
                channel.basicAck(tag, false);
                return;
            }

            AmqpMail amqpMail = (AmqpMail) message.getPayload();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

            messageHelper.setFrom(mailProperties.getUsername());
            messageHelper.setTo(amqpMail.getMail());
            messageHelper.setSubject("账号注册成功通知");
            messageHelper.setSentDate(Date.from(amqpMail.getCreatedTime().atZone(ZoneId.systemDefault()).toInstant()));

            Context context = new Context();
            context.setVariable("username", amqpMail.getUsername());
            context.setVariable("loginName", amqpMail.getLoginName());

            // mail  是邮件 html 模板的文件名
            String mail = templateEngine.process("mail", context);
            messageHelper.setText(mail, true);
            mailSender.send(mimeMessage);

            // 存入缓存
            hashOperations.put(MailConstants.CACHE_KEY,  msgId, "OK");

            // 手动确认消息
            channel.basicAck(tag, false);
            log.info("用户{}的注册邮件发送成功", amqpMail.getUsername());
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            /**
             * 手动确认消息
             * tag：消息序号
             * multiple：是否确认多条
             * requeue：是否退回到队列
             */
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                log.error("邮件发送失败", ex);
            }
        }
    }
}
