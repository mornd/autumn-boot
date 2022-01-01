package com.mornd.system.constant;

/**
 * @author mornd
 * @dateTime 2021/11/8 - 10:55
 */
public interface SpringSecurityConstant {
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

        //后台请求url
        "/userLogin",
        "/captcha",
        "/css/**",
        "/js/**",
        "/index.html",
    };

    //系统角色前缀
    String ROLE_PREFIX = "ROLE_";
}
