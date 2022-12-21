package com.mornd.system.controller;

import com.mornd.system.annotation.Anonymous;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Value("${spring.application.name}")
    private String appName;
//    @Value("${server.servlet.context-path}")
//    private String contextPath;

    /**
     * 访问首页，提示语
     */
    @Anonymous
    @RequestMapping("/")
    public String index()
    {
        return String.format("欢迎使用%s后台管理框架，请通过前端地址访问。", appName);
    }
}
