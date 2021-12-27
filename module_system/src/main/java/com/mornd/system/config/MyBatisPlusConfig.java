package com.mornd.system.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 10:48
 */
@Configuration
@MapperScan({"com.mornd.**.mapper"})
public class MyBatisPlusConfig {

    /**
     * 分页
     * @return
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //分页拦截器
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        //最大单页限制数量
        paginationInnerInterceptor.setMaxLimit(1000L);
        return mybatisPlusInterceptor;
    }
}
