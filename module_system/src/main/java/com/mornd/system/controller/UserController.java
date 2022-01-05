package com.mornd.system.controller;

import com.mornd.system.entity.dto.ChangePwdDTO;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.SecretUtil;
import com.mornd.system.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2021/9/17 - 8:26
 */
@Validated
@RestController
@RequestMapping("/sysUser")
@Api("系统用户接口")
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/getLoginUser")
    public JsonResult getUserInfo(){
        return JsonResult.successData(SecurityUtil.getLoginUser());
    }

    @ApiOperation("匹配当前密码")
    @GetMapping("/verifyCurrentPassword/{oldPwd}")
    public JsonResult verifyCurrentPassword(@PathVariable String oldPwd) {
        boolean matches = userService.verifyCurrentPassword(SecretUtil.desEncrypt(oldPwd));
        return JsonResult.successData(matches);
    }

    @ApiOperation("修改密码")
    @PostMapping("/changePwd")
    public JsonResult changePwd(@RequestBody @Validated ChangePwdDTO pwd) {
        return userService.changePwd(SecretUtil.desEncrypt(pwd.getOldPwd()), SecretUtil.desEncrypt(pwd.getNewPwd()));
    }
}
