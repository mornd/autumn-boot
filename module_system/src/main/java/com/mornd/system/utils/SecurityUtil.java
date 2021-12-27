package com.mornd.system.utils;

import com.mornd.system.entity.po.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author mornd
 * @dateTime 2021/9/17 - 8:07
 * security工具类
 */
@Slf4j
public class SecurityUtil {

    /**
     * 从security中获取当前登录用户信息
     * @return
     */
    public static SysUser getLoginUser(){
        try {
            SysUser principal = (SysUser) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            principal.setPassword(null);
            return principal;
        } catch (Exception e) {
            log.error("获取当前登录用户信息发生异常！{}", e.getMessage());
            throw new RuntimeException("用户未登录！");
        }
    }

    /**
     * 获取当前登录用户名
     * @return
     */
    public static String getLoginUsername(){
        return getLoginUser().getLoginName();
    }

    /**
     * 获取当前登录用户id
     * @return
     */
    public static String getLoginUserId(){
        return getLoginUser().getId();
    }

    /**
     * 获取当前用户的加密形式密码
     * @return
     */
    public static String getPassword() {
        SysUser principal = (SysUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return principal.getPassword();
    }
}
