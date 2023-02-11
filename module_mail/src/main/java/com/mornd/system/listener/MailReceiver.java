package com.mornd.system.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.system.entity.AmqpMail;
import com.mornd.system.constants.MailConstants;
import com.mornd.system.entity.MailLog;
import com.mornd.system.mapper.MailLogMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private TemplateEngine templateEngine;

    @Resource
    private MailLogMapper mailLogMapper;

    /**
     * rabbit 邮件监听
     * @param message 消息
     * @param channel 用于手动确认
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MailConstants.QUEUE_NAME, durable = "true"),
            exchange = @Exchange(name = MailConstants.EXCHANGE_NAME, type = ExchangeTypes.DIRECT, durable = "true", autoDelete = "false"),
            key = MailConstants.ROUTING_KEY_NAME
    ))
    public void handle(Message message, Channel channel) throws Exception {
        try {
            // 消息 id
             String msgId = (String) message.getHeaders().get("spring_returned_message_correlation");
            // 手动确认
            // channel.basicAck((long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG), false);

            // 消息体
            AmqpMail amqpMail = (AmqpMail) message.getPayload();

            LambdaQueryWrapper<MailLog> qw = Wrappers.lambdaQuery(MailLog.class);
            qw.eq(MailLog::getMsgId, msgId);
            qw.eq(MailLog::getStatus, MailLog.MailLogStatus.SUCCESS.ordinal());
            Integer count = mailLogMapper.selectCount(qw);
            if(count > 0) {
                return;
            }

            sendMail(amqpMail);

            // 更新数据库
            MailLog mailLog = new MailLog();
            mailLog.setMsgId(msgId);
            mailLog.setStatus(MailLog.MailLogStatus.SUCCESS.ordinal());
            mailLog.setUpdateTime(LocalDateTime.now());
            mailLogMapper.updateById(mailLog);

            log.info("用户{}的注册邮件发送成功", amqpMail.getLoginName());
        } catch (Exception e) {
            log.error("用户的邮件发送失败", e);
            // 继续向上抛出异常，让 spring 的 retry 机制处理
            throw new RuntimeException(e);
        }
    }

    /**
     * api 发送邮件
     * @param amqpMail
     * @throws MessagingException
     */
    private void sendMail(AmqpMail amqpMail) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

        messageHelper.setFrom(mailProperties.getUsername());
        messageHelper.setTo(amqpMail.getMail());
        messageHelper.setSubject("账号注册成功通知");
        // Date.from(amqpMail.getCreatedTime().atZone(ZoneId.systemDefault()).toInstant());
        messageHelper.setSentDate(new Date());

        Context context = new Context();
        context.setVariable("username", amqpMail.getUsername());
        context.setVariable("loginName", amqpMail.getLoginName());
        context.setVariable("dateTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(amqpMail.getCreatedTime()));

        // mail  是邮件 html 模板的文件名
        String mail = templateEngine.process("mail", context);
        messageHelper.setText(mail, true);
        mailSender.send(mimeMessage);
    }
}
