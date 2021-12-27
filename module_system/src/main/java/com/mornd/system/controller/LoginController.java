package com.mornd.system.controller;

import com.mornd.system.entity.dto.LoginUserDTO;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 12:42
 * 登录
 */
@Slf4j
@Api("用户认证接口")
@RestController
@RequestMapping
public class LoginController {
    @Resource
    private LoginService loginService;

    /**
     * 用户登录
     * @param loginUserDTO
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/userLogin")
    public JsonResult userLogin(@RequestBody @Validated LoginUserDTO loginUserDTO){
        return loginService.userLogin(loginUserDTO);
    }

    @ApiOperation("用户注销")
    @PostMapping("/userLogout")
    public JsonResult userLogout(HttpServletRequest request, HttpServletResponse response){
        return loginService.userLogout(request, response);
    }
}
