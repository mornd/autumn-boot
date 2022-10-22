package com.mornd.system.service;

import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.dto.LoginUserDTO;
import com.mornd.system.entity.result.JsonResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:25
 */
public interface AuthService {
    /**
     * 本地用户登录
     * @param loginUserDTO
     * @return
     */
    JsonResult userLogin(LoginUserDTO loginUserDTO);

    /**
     * 通用登录方法，并生成 token
     * @param authUser security 用户
     * @return token
     */
    String genericLogin(AuthUser authUser);

    JsonResult userLogout(HttpServletRequest request);
}
