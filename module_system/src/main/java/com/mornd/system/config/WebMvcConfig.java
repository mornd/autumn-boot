package com.mornd.system.config;

import com.mornd.system.constant.GlobalConst;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2022/5/20 - 1:38
 * web mvc 配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private AutumnConfig autumnConfig;

    /**
     * 添加静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(GlobalConst.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + autumnConfig.getProfile() + "/");
    }

    /**
     * 跨域配置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //允许所有的访问请求（访问路径）
        registry.addMapping("/**")
                // 允许跨域的方法
                .allowedMethods("GET","POST","DELETE","PUT","PATCH","HEAD","OPTIONS")
                //允许所有的请求header访问
                .allowedHeaders("*")
                //允许所有的请求域名访问跨域资源
                //.allowedOrigins("*")
                .allowedOriginPatterns("*")
                // 是否允许证书
                .allowCredentials(true)
                // 预检间隔时间
                .maxAge(3600);
    }
}
