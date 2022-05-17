package com.mornd.system.config.security.components;

import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 16:47
 * token 拦截过滤器
 */
@Slf4j
@Component
public class TokenAuthorizationFilter extends OncePerRequestFilter {
    @Resource
    private TokenProvider tokenProvider;
    @Resource
    private TokenProperties tokenProperties;
    @Resource
    private RedisUtil redisUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取token
        String token = tokenProvider.searchToken(request);
        if(StringUtils.hasText(token)) {
            // 查找缓存数据
            try {
                AuthUser authUser = (AuthUser) redisUtil.getValue(tokenProperties.getOnlineUserKey() + token);
                if(Objects.nonNull(authUser)) {
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    // todo token 续期
                    if(tokenProperties.getIsRenewal()) {
                        // 获取 token 续订过期时间
                        long terminateRenewalTime = tokenProvider.getTerminateRenewalTime(token);
                        if(System.currentTimeMillis() < terminateRenewalTime) {
                            // 刷新 token 的过期时间
                            redisUtil.expire(tokenProperties.getOnlineUserKey() + token, tokenProperties.getExpiration(), TimeUnit.MILLISECONDS);    
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        // 放行至下一个过滤器
        filterChain.doFilter(request, response);
    }
}
