package com.mornd.system.constant;

/**
 * @author mornd
 * @dateTime 2021/11/8 - 10:55
 */
public interface SecurityConst {
    //springSecurity的URL白名单
    String[] NONE_SECURITY_URL_PATTERNS = {
            //图标
            "favicon.ico",
            //swagger相关
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v2/api-docs-ext",

            //druid数据源监控页面
            "/druid/**",

            // websocket 连接
            //"/ws/**",

            //后台请求url
            "/userLogin",
            "/captcha",
            "/css/**",
            "/js/**",
            "/index.html",
    };

    //系统角色前缀
    String ROLE_PREFIX = "ROLE_";

    //超级管理员id
    String SUPER_ADMIN_ID = "1425011630752735234";

    //菜单管理id
    String MENU_ID = "1425384413584252930";

    //新增用户默认密码
    String USER_DEFAULT_PWD = "000";
}
