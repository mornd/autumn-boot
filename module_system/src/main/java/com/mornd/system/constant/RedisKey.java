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

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 短信验证码
     */
    public static final String PHONE_MSG_CODE = "phone_msg_code:";

    /**
     * 忘记密码功能短信验证码
     */
    public static final String FORGET_PWD_PHONE_MSG_CODE = "forget_pwd_phone_msg_code:";
}
