package com.mornd.system.config.security.custom;

import com.mornd.system.constant.SecurityConst;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author mornd
 * @dateTime 2022/10/16 - 21:08
 * 权限控制 （根据请求url分析请求所需的角色）
 */

@Deprecated
@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    public static final String DEFAULT_LOGIN_ROLE = "ROLE_LOGIN";

    /**
     * 根据访问的 url 查询权限表，返回该 url 所需要的角色集合
     * @param object the object being secured
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        String requestURI = request.getRequestURI();
        // 获取请求的 url (例如：/sysUser?pageNo=1&pageSize=10&total=0)
        String requestUrl = filterInvocation.getRequestUrl();
        // 放行的url pattern
        String[] patterns = new String[] {"/captcha**"};
        for (String pattern : patterns) {
            if(antPathMatcher.match(pattern, requestUrl)) {
                // 放行的 url 最终返回长度为0的数组，并且不执行 AccessDecisionManager 的方法
                return SecurityConfig.createListFromCommaDelimitedString(null);
            }
        }
        // todo 获取所有菜单，其中每个菜单中包含所能访问的角色列表，这里用作示例，伪代码
        List<SysPermission> pers = new ArrayList<>();
        // pers = permission.getPersWithRoles();
        for (SysPermission per : pers) {
            // path 的值为(或者在表中新加字段：url) /system/user/** 必须是正则表达式
            if(antPathMatcher.match(per.getPath(), requestUrl)) {
                String[] roleNames = new String[]{"admin"};
                //String[] roleNames = per.getRoles().stream().map(SysRole::getCode).toArray(String[]::new);
                return SecurityConfig.createList(roleNames);
            }
        }
        // 没匹配的 url 默认登录即可访问
        return SecurityConfig.createList(DEFAULT_LOGIN_ROLE);
        // return getAttributes(object);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }
}
