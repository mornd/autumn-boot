package com.mornd.system.controller;

import com.mornd.system.annotation.Anonymous;
import com.mornd.system.entity.dto.OtherLoginUseDTO;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.OtherLoginService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2022/10/19 - 22:11
 * 其它登录方式处理
 */
@Anonymous
@RestController
public class OtherLoginController {
    @Resource
    private OtherLoginService otherLoginService;

    /**
     * gitee 方式登录
     * @return
     */
    @GetMapping("/preLoginByGitee")
    public JsonResult preLoginByGitee() {
        return otherLoginService.preLoginByGitee();
    }

    @PostMapping("/loginByGitee")
    public JsonResult loginByGitee(@RequestBody @Validated OtherLoginUseDTO user) {
        return otherLoginService.loginByGitee(user);
    }
}
