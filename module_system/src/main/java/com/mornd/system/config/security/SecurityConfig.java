package com.mornd.system.config.security;

import com.mornd.system.config.security.components.TokenAuthorizationFilter;
import com.mornd.system.constant.SecurityConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 16:48
 * security 核心配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private TokenAuthorizationFilter tokenAuthorizationFilter;
    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * security 核心配置方法
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 禁用 csrf
                .csrf().disable()
                // 允许跨域
                .cors()
                .and()
                // 不使用 http session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 配置白名单
                .antMatchers(SecurityConst.NONE_SECURITY_URL_PATTERNS).permitAll()
                // 其他请求都需要认证
                .anyRequest().authenticated()
                .and()
                // 配置 token 过滤器，在用户名密码校验之前校验 token
                .addFilterBefore(tokenAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                // 没有登录，没有权限访问的异常处理
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                // 关闭缓存
                .headers().cacheControl();
        
        // 禁用 security 提供的 /logout 请求路径
        httpSecurity.logout().disable();
        // 防止 iframe 造成跨域
        httpSecurity.headers().frameOptions().disable();
    }

    /**
     * 用于密码的加解密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
}
