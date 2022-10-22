package com.mornd.system.utils;

import com.mornd.system.config.security.components.TokenProvider;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.entity.dto.AuthUser;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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

    /**
     * 获取存储在 redis 中的当前登录用户信息 key
     * @param token token
     * @return
     */
    public String getLoginUserRedisKey(String token) {
        return String.format("%s-%s", RedisKey.ONLINE_USER_KEY, token);
    }

    /**
     * 用户登录时生成其它信息
     * @param user
     * @return
     */
    public AuthUser generateLoginInfo(AuthUser user) {
        user.setIp(IpUtils.getIpAddr(request));
        user.setOs(NetUtil.getOs(request));
        user.setBrowser(NetUtil.getBrowser(request));
        user.setAddress(AddressUtils.getRealAddressByIP(user.getIp()));
        user.setLoginTime(new Date());
        return user;
    }

    /**
     * 清空当前缓存中保存的登录用户信息
     */
    public void delCacheLoginUser() {
        String token = tokenProvider.searchToken(request);
        redisUtil.delete(getLoginUserRedisKey(token));
    }
}
