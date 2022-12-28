package com.mornd.system.constant;

/**
 * @author: mornd
 * @dateTime: 2022/12/3 - 19:12
 * @description: 正则表达式常量类
 */
public class RegexpConstant {
    // 手机号码校验
    public static final String PHONE = "1[3,4,5,6,7,8,9]\\d{9}$";

    //  登录账号 (字母开头，允许2-20字节，包含2，20，允许字母数字下划线)
    public static final String ACCOUNT = "^[a-zA-Z][a-zA-Z0-9_]{1,19}$";

    public static final String ACCOUNT_MESSAGE = "账号须由2-20个字母、数字、下划线组成，且以字母开头";
}
