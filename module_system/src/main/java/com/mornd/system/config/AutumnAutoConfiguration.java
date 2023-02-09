package com.mornd.system.config;

//import com.mornd.system.task.MailTask;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: mornd
 * @dateTime: 2023/2/4 - 14:05
 */

@Configuration
@EnableConfigurationProperties(AutumnConfig.class)
public class AutumnAutoConfiguration {

    /**
     * 邮件任务
     * @return
     */
//    @Bean
//    @ConditionalOnProperty(value = "autumn.user-mail-notification")
//    public MailTask mailTask() {
//        return new MailTask();
//    }
}
