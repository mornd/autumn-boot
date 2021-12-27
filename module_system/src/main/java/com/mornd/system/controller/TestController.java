package com.mornd.system.controller;

import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.utils.SecurityUtil;
import com.mornd.system.validation.SelectValidGroup;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 21:53
 */
@Validated
@RestController
public class TestController {
    //验证单个参数
    @PreAuthorize("hasAnyRole('superadmin')")
    @GetMapping("/test")
    public JsonResult test(@NotBlank String name){
        SysUser loginUser = SecurityUtil.getLoginUser();
        return JsonResult.success(name);
    }

    /**
     * 验证实体bean
     * @param sysUser
     * @return
     */

    //@PreAuthorize("hasAuthority('admin:read')")
    //@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/valid1")
    public JsonResult valid1(@RequestBody @Validated(SelectValidGroup.class) SysUser sysUser){
        System.out.println(sysUser);
        return JsonResult.success();
    }

    @GetMapping("/valid2")
    //NotEmpty 数组不能为空，并且数组长度大于等于1
    public JsonResult valid2(@NotEmpty(message = "集合不能为空！") Integer[] list){
        System.out.println(list);
        return JsonResult.success("200");
    }

    @PreAuthorize("hasAuthority('Per_System')")
    @GetMapping("test1")
    public String hi(){
        return "hi";
    }
}
