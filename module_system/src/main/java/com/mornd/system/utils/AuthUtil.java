package com.mornd.system.utils;

import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.config.security.components.TokenProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author mornd
 * @dateTime 2022/5/3 - 1:37
 */
@Component
public class AuthUtil {
    @Resource
    private TokenProvider tokenProvider;
    @Resource
    private HttpServletRequest request;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private TokenProperties tokenProperties;

    /**
     * 获取存储在 redis 中的当前登录用户信息 key
     * @param token token
     * @return
     */
    public String getLoginUserRedisKey(String token) {
        String subject = tokenProvider.getSubject(token);
        return String.format("%s%s-%s",tokenProperties.getOnlineUserKey(), subject, token);
    }


    /**
     * 清空当前缓存中保存的登录用户信息
     */
    public void delCacheLoginUser() {
        String token = tokenProvider.searchToken(request);
        redisUtil.delete(getLoginUserRedisKey(token));
    }
}
