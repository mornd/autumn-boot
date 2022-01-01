package com.mornd.system.constant;

/**
 * redis前缀常量
 * @author mornd
 * @dateTime 2021/9/6 - 9:13
 */
public interface RedisKey {
    /**
     * 验证码key
     */
    String LOGIN_CAPTCHA_KEY = "login_captcha_value";

    /**
     * 当前登录用户信息
     */
    String CURRENT_USER_INFO_KEY = "current_login_user_";
}
