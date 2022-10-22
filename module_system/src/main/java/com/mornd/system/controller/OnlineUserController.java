package com.mornd.system.controller;

import com.mornd.system.annotation.LogStar;
import com.mornd.system.entity.po.OnlineUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.OnlineUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 19:27
 */

@RestController
@RequestMapping("/onlineUser")
public class OnlineUserController {
    @Resource
    private OnlineUserService onlineUserService;

    @PreAuthorize("hasAuthority('onlineUser')")
    @LogStar("获取在线用户列表")
    @GetMapping
    public JsonResult<?> pageList(@Validated OnlineUser user) {
        return onlineUserService.pageList(user);
    }

    /**
     * 强制踢人
     * @param loginName
     * @return
     */
    @PreAuthorize("hasAuthority('onlineUser:kick')")
    @LogStar("强制踢人")
    @DeleteMapping("/{loginName}")
    public JsonResult<?> kick(@PathVariable String loginName) {
        onlineUserService.kick(loginName);
        return JsonResult.success("操作成功");
    }
}
