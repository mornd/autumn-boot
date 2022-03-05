package com.mornd.system.controller;

import com.mornd.system.annotation.LogStar;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysLogVO;
import com.mornd.system.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
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
    
    @ApiOperation("获取列表数据")
    @GetMapping
    public JsonResult pageList(SysLogVO log) {
        return sysLogService.pageList(log);    
    }

    @ApiOperation("清空表数据")
    @DeleteMapping
    @LogStar(value = "清空表数据")
    public JsonResult clearAll() {
        boolean res = sysLogService.clearAll();
        if(res) {
            return JsonResult.success();
        }
        return JsonResult.failure("操作失败");
    }
}
