package com.mornd.system.config;

import com.mornd.system.filter.RepeatableFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author mornd
 * @dateTime 2022/10/30 - 12:35
 * servlet 过滤器配置
 */

@Configuration
public class FilterConfig {

    /**
     * 注册过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        // 添加过滤器
        registrationBean.setFilter(new RepeatableFilter());
        // 名称
        registrationBean.setName("repeatableFilter");
        // 拦截路径
        registrationBean.addUrlPatterns("/*");
        // 设置过滤器执行顺序
        registrationBean.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registrationBean;
    }
}
