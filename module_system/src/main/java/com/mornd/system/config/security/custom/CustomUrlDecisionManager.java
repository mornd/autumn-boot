package com.mornd.system.config.security.custom;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author mornd
 * @dateTime 2022/10/16 - 22:09
 * 判断用户角色
 * 该项目暂时未使用
 */
@Deprecated
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {
    /**
     *
     * @param authentication the caller invoking the method (not null)
     * @param object the secured object being called
     * @param configAttributes CustomAuthFilter.getAttributes 方法的的返回值
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        for (ConfigAttribute configAttribute : configAttributes) {
            // 当前 url 所需要的角色
            String needRole = configAttribute.getAttribute();
            // 判断角色是否是登录即可访问的角色，此角色在 CustomAuthFilter 中配置
            if(CustomSecurityMetadataSource.DEFAULT_LOGIN_ROLE.equals(needRole)) {
                if(authentication instanceof AnonymousAuthenticationToken) {
                    throw new AccessDeniedException("尚未登录，请先登录 -- CustomUrlDecisionManager");
                } else {
                    return;
                }
            } else {
                // 判断用户角色是否为 url 所需角色
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                for (GrantedAuthority authority : authorities) {
                    if(authority.getAuthority().equals(needRole)) {
                        return;
                    }
                }
            }
            throw new AccessDeniedException("权限不足，请联系管理员 -- CustomUrlDecisionManager");
        }
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }
}
