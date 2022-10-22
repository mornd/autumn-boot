package com.mornd.system.constant;

/**
 * redis前缀常量
 * @author mornd
 * @dateTime 2021/9/6 - 9:13
 */
public class RedisKey {
    /**
     * 验证码key
     */
    public static final String LOGIN_CAPTCHA_KEY = "login-captcha-";

    /**
     * 缓存中保存在线用户的 key
     */
    public static final String ONLINE_USER_KEY = "online-user-";
}
