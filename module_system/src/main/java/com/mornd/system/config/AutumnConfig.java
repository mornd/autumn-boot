package com.mornd.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author mornd
 * @dateTime 2022/10/22 - 17:32
 */
@Data
@ConfigurationProperties(prefix = "autumn")
public class AutumnConfig {
    private String author;
    private String version;
    private String applicationName;
    private Integer uploadStorage;
    private String profile;

    /**
     * 用户注册成功时是否发送通知邮件
     */
    private boolean userMailNotification;
}
