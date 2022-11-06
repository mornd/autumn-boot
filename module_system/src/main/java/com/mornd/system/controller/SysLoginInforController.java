package com.mornd.system.controller;

import com.mornd.system.annotation.LogStar;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysLoginInforVO;
import com.mornd.system.service.SysLoginInforService;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2022/11/6 - 19:33
 */
@RestController
@RequestMapping("/sysLoginInfor")
public class SysLoginInforController {
    @Resource
    private SysLoginInforService sysLoginInforService;

    @PreAuthorize("hasAnyAuthority('systemMonitor:sysLoginInfor')")
    @LogStar("查看登录日志表")
    @GetMapping
    public JsonResult<?> pageList(SysLoginInforVO sysLoginInforVO) {
        return sysLoginInforService.pageList(sysLoginInforVO);
    }

    @LogStar("用户查看自己的登录日志")
    @GetMapping("/currentUser")
    public JsonResult<?> getCurrentUserList(SysLoginInforVO sysLoginInforVO) {
        sysLoginInforVO.setUserId(SecurityUtil.getLoginUserId());
        return sysLoginInforService.pageList(sysLoginInforVO);
    }

    @PreAuthorize("hasAnyAuthority('systemMonitor:sysLoginInfor:truncate')")
    @LogStar("清空登录日志表")
    @DeleteMapping
    public JsonResult truncate() {
        sysLoginInforService.truncate();
        return JsonResult.success();
    }
}
