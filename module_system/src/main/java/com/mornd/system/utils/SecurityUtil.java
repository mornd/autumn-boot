package com.mornd.system.utils;

import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author mornd
 * @dateTime 2021/9/17 - 8:07
 * security工具类
 */
@Slf4j
public class SecurityUtil {
    /**
     * 获取认证对象
     * @return
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 从security中获取当前登录用户信息
     * @return
     */
    public static SysUser getLoginUser(){
        try {
            AuthUser principal = (AuthUser) getAuthentication().getPrincipal();
            SysUser sysUser = principal.getSysUser();
            return sysUser;
        } catch (Exception e) {
            log.error("获取当前登录用户信息发生异常！{}", e.getMessage());
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息");
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
}
