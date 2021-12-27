package com.mornd.system.service;

import com.mornd.system.entity.dto.LoginUserDTO;
import com.mornd.system.entity.result.JsonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mornd
 * @dateTime 2021/9/6 - 8:56
 */
public interface LoginService {
    JsonResult userLogin(LoginUserDTO user);

    JsonResult userLogout(HttpServletRequest request, HttpServletResponse response);
}
