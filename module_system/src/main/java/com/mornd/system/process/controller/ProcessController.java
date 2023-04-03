package com.mornd.system.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.controller.base.BaseController;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.process.entity.Process;
import com.mornd.system.process.entity.vo.ProcessFormVo;
import com.mornd.system.process.entity.vo.ProcessVo;
import com.mornd.system.process.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: mornd
 * @dateTime: 2023/3/29 - 14:17
 */

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController extends BaseController {
    private final ProcessService processService;

    @GetMapping
    @LogStar(value = "查询流程列表", businessType = LogType.SELECT)
    public JsonResult pageList(ProcessVo vo) {
        IPage<ProcessVo> page =  processService.pageList(vo);
        return JsonResult.successData(page);
    }

    @PostMapping("/startup")
    @LogStar(title = "启动流程实例")
    public JsonResult startup(@RequestBody @Validated ProcessFormVo vo) {
        processService.startup(vo);
        return JsonResult.success("流程启动成功");
    }

    @GetMapping("/findPending")
    @LogStar(title = "用户查询当前流程待办", businessType = LogType.SELECT)
    public JsonResult findPending(Process process) {
        IPage<ProcessVo> page = processService.findPending(process);
        return JsonResult.successData(page);
    }
}
