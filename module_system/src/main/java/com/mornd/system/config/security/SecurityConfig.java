package com.mornd.system.config.security;

import com.mornd.system.config.PermitAllUrlProperties;
import com.mornd.system.config.security.components.TokenAuthorizationFilter;
import com.mornd.system.config.security.components.CustomFilter;
import com.mornd.system.constant.SecurityConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
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
     * 允许匿名访问的地址
     */
    @Resource
    private PermitAllUrlProperties permitAllUrl;
    @Resource
    private FilterInvocationSecurityMetadataSource metadataSource;
    @Resource
    private AccessDecisionManager accessDecisionManager;
    @Resource
    private CustomFilter customFilter;

    /**
     * security 核心配置方法
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 注解标记允许匿名访问的url
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity.authorizeRequests();
        permitAllUrl.getUrls().forEach(url -> registry.antMatchers(url).permitAll());

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
                // 除了白名单，其他请求都需要认证
                .anyRequest().authenticated()
                // 动态权限配置
//                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
//                    @Override
//                    public <O extends FilterSecurityInterceptor> O postProcess(O obj) {
//                        obj.setAccessDecisionManager(accessDecisionManager);
//                        obj.setSecurityMetadataSource(metadataSource);
//                        return obj;
//                    }
//                })
                .and()
                // 配置 token 过滤器，在用户名密码校验之前校验 token
                .addFilterBefore(tokenAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(customFilter, TokenAuthorizationFilter.class)
                // 添加 没有登录，没有权限访问的异常处理
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

    /**
     * 登录方式二：
     *
     * Security 配置类中注入 AuthenticationManager
     * @Bean
     * @Override
     * public AuthenticationManager authenticationManagerBean() throws Exception {
     *     return super.authenticationManagerBean();
     * }
     *
     * // UserDetailsServiceImpl 中调用 AuthenticationManager 的方法
     * @Resource
     * private AuthenticationManager authenticationManager;
     *
     * Authentication authenticate2 = authenticationManager.authenticate(authenticationToken);
     */
}
