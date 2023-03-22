package com.mornd.system.controller;

import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysLogVO;
import com.mornd.system.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2022/1/28 - 16:06
 */

@Api("系统日志接口")
@RestController
@RequestMapping("/sysLog")
public class SysLogController {
    @Resource
    private SysLogService sysLogService;

    @LogStar(value = "查看操作日志", BusinessType = LogType.SELECT)
    @PreAuthorize("hasAnyAuthority('systemMonitor:sysLog')")
    @ApiOperation("获取列表数据")
    @GetMapping
    public JsonResult pageList(SysLogVO log) {
        return sysLogService.pageList(log);
    }

    @PreAuthorize("hasAnyAuthority('systemMonitor:sysLog:clear')")
    @ApiOperation("清空表数据")
    @DeleteMapping
    @LogStar(value = "清空操作日志表", BusinessType = LogType.CLEAR)
    public JsonResult clearAll() {
        sysLogService.clearAll();
        return JsonResult.success();
    }
}
