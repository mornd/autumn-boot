package com.mornd.system.config.security;

import com.mornd.system.config.security.components.JwtAuthorizationTokenFilter;
import com.mornd.system.config.security.components.ResultAccessDeniedHandler;
import com.mornd.system.config.security.components.ResultAuthenticationEntryPoint;
import com.mornd.system.constants.SpringSecurityConstant;
import com.mornd.system.service.SysUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2021/9/3 - 15:56
 * springSecurity配置类
 */

@Configuration
//开启权限注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class securityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ResultAccessDeniedHandler accessDeniedHandler;
    @Resource
    private ResultAuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    private JwtAuthorizationTokenFilter authorizationTokenFilter;

    /**
     * 设置放行白名单
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(SpringSecurityConstant.NONE_SECURITY_URL_PATTERNS);
    }

    /**
     * security核心配置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //基于jwt，舍弃csrf
        http.csrf().disable()
            //设置允许跨域访问，否则token无效或者为空，提示未登录
            .cors()
            .and()
            //基于jwt，舍弃session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            //设置所有请求需都要认证（除白名单外）
            .authorizeRequests()
            //配置白名单
            //.antMatchers("xxx").permitAll()
            .anyRequest().authenticated()
            .and()
            //token过滤器，在用户名密码校验之前校验token
            .addFilterBefore(authorizationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            //没有登录，没有权限访问自定义返回结果
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler)
            .and()
            //关闭缓存
            .headers().cacheControl();
    }

    /**
     * 实现自定义登录逻辑
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    /**
     * 用户登录
     * @return
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        //重写loadUserByUsername(username)方法
        return username -> sysUserService.findByUsername(username);
    }

    /**
     * 用于密码的加解密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
