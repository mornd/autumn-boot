package com.mornd.system.config;

import com.mornd.system.constant.GlobalConst;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.interceptor.RepeatSubmitInterceptor;
import com.mornd.system.interceptor.UnsupportedOperationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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

    @Resource
    private UnsupportedOperationInterceptor unsupportedOperationInterceptor;

    @Resource
    private RepeatSubmitInterceptor repeatSubmitInterceptor;

    /**
     * 添加 spring 拦截器
     * 执行逻辑：请求 -> filter -> interceptor -> interceptor -> filter -> 响应
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(unsupportedOperationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(SecurityConst.NONE_SECURITY_URL_PATTERNS)
                .order(0); // 默认根据注册顺序执行，若指定order，则根据 order 升序执行

        registry.addInterceptor(repeatSubmitInterceptor)
                .addPathPatterns("/**")
                .order(1);
    }
}
