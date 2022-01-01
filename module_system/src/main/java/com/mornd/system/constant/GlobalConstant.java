package com.mornd.system.constant;

/**
 * @author mornd
 * @dateTime 2021/11/8 - 9:56
 * 全局常量
 */
public interface GlobalConstant {
    //全局常量：启用
    Integer ENABLED = 1;
    //全局常量：停用
    Integer DISABLED = 0;
    
    //一级菜单的parent_id
    String MENU_PARENT_ID = "0";

    /**
     * 验证码过期时间（单位：分钟）
     */
    Integer CAPTCHA_EXPIRATION = 3;
}
