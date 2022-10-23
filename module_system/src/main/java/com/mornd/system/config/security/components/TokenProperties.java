package com.mornd.system.config.security.components;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:56
 * jwt 配置 token 的一些动态属性，用户可修改的属性
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class TokenProperties {
    /**
     * JWT 存储的请求头
     */
    private String tokenHeader;
    /**
     * JWT 加解密使用的密钥(盐)
     */
    private String secret;
    /**
     * JWT 负载拿到开头
     */
    private String tokenHead;
    /**
     * token 不操作时的过期时间
     */
    private Long expiration;

    /**
     * 时间单位
     */
    private TimeUnit expirationTimeUnit;
    /**
     * 是否是单用户登录模式
     */
    private Boolean singleLogin;
}
