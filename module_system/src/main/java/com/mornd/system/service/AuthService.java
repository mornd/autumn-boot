package com.mornd.system.service;

import com.mornd.system.entity.dto.LoginUserDTO;
import com.mornd.system.entity.result.JsonResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:25
 */
public interface AuthService {
    JsonResult userLogin(LoginUserDTO loginUserDTO);

    JsonResult userLogout(HttpServletRequest request);
}
