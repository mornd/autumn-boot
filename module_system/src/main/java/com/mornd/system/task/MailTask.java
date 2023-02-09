//package com.mornd.system.task;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.mornd.system.AmqpMail;
//import com.mornd.system.constants.MailConstants;
//import com.mornd.system.entity.po.MailLog;
//import com.mornd.system.entity.po.SysUser;
//import com.mornd.system.mapper.MailLogMapper;
//import com.mornd.system.mapper.UserMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.connection.CorrelationData;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import javax.annotation.Resource;
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * @author: mornd
// * @dateTime: 2023/2/4 - 13:50
// */
//
//@Slf4j
//public class MailTask {
//
//    @Resource
//    private MailLogMapper mailLogMapper;
//    @Resource
//    private RabbitTemplate rabbitTemplate;
//    @Resource
//    private UserMapper userMapper;
//
//
//    /**
//     * 10s 执行一次
//     */
//    @Scheduled(cron = "0/10 * * * * ?")
//    public void handle() {
//        LambdaQueryWrapper<MailLog> qw = Wrappers.lambdaQuery(MailLog.class);
//        qw.eq(MailLog::getStatus, MailConstants.DELIVERING);
//        qw.lt(MailLog::getTryTime, LocalDateTime.now());
//
//        List<MailLog> list = mailLogMapper.selectList(qw);
//
//        log.info("正在执行邮件任务，状态为投递中的邮件总数：{}", list.size());
//
//        for (MailLog mailLog : list) {
//            if(mailLog.getTryCount() >= MailConstants.MAX_TRY_COUNT) {
//                // 消息投递失败次数大于重试次数，不再投递
//                mailLog.setStatus(MailConstants.FAILURE);
//                mailLog.setUpdateTime(LocalDateTime.now());
//                mailLogMapper.updateById(mailLog);
//            } else {
//                // 重新投递
//                mailLog.setTryCount(mailLog.getTryCount() + 1);
//                mailLog.setTryTime(LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT));
//                mailLog.setUpdateTime(LocalDateTime.now());
//
//                mailLogMapper.updateById(mailLog);
//
//                // 查询接收用户
//                SysUser sysUser = userMapper.selectOne(Wrappers.lambdaQuery(SysUser.class)
//                        .eq(SysUser::getId, mailLog.getUserId()));
//
//                AmqpMail amqpMail = new AmqpMail();
//                amqpMail.setUsername(sysUser.getRealName());
//                amqpMail.setLoginName(sysUser.getLoginName());
//                amqpMail.setMail(sysUser.getEmail());
//                amqpMail.setCreatedTime(LocalDateTime.now());
//                // 发送消息
//                rabbitTemplate.convertAndSend(MailConstants.EXCHANGE_NAME,
//                        MailConstants.ROUTING_KEY_NAME,
//                        amqpMail,
//                        new CorrelationData(mailLog.getMsgId()));
//            }
//
//
//        }
//
//    }
//}
