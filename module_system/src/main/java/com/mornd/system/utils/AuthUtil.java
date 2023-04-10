package com.mornd.system.utils;

import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.service.OnlineUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2022/5/3 - 1:37
 */
@Component
public class AuthUtil {
    @Resource
    private TokenProperties tokenProperties;
    @Resource
    private HttpServletRequest request;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private OnlineUserService onlineUserService;

    /**
     * 获取存储在 redis 中的当前登录用户信息 key
     * @param token token
     * @return
     */
    public String getLoginUserRedisKey(String token) {
        return RedisKey.ONLINE_USER_KEY + token;
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

    public void deleteCacheUser() {
        this.deleteCacheUser(SecurityUtil.getLoginUserId());
    }

    /**
     * 根据id清空当前缓存中保存的登录用户信息
     * @param id
     */
    public void deleteCacheUser(String id) {
        onlineUserService.kick(id);
    }

    /**
     *  根据用户id，更新缓存的用户信息
     * @param id
     * @param principal
     */
    public void updateCacheUser(String id, AuthUser principal) {
        Set<String> keys = onlineUserService.getOnlineUserKeyById(id);
        for (String key : keys) {
            // 获取key还有多长时间过期
            long expire = redisUtil.getExpire(key, tokenProperties.getExpirationTimeUnit());
            // 更新key的值
            redisUtil.setValue(key, principal, expire, tokenProperties.getExpirationTimeUnit());
        }
    }
}
