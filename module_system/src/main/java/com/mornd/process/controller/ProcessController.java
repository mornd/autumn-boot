package com.mornd.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.process.entity.vo.ApprovalVo;
import com.mornd.process.entity.vo.ProcessVo;
import com.mornd.process.service.ProcessService;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.controller.base.BaseController;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.exception.BadRequestException;
import com.mornd.process.entity.Process;
import com.mornd.process.entity.vo.ProcessFormVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.mornd.process.entity.Process.ApproveStatus.AGREE;
import static com.mornd.process.entity.Process.ApproveStatus.REJECT;

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
    @LogStar(title = "用户查询当前待处理流程", businessType = LogType.SELECT)
    public JsonResult findPending(Process process) {
        IPage<Process> page = processService.findPending(process);
        return JsonResult.successData(page);
    }

    @GetMapping("/findProcessed")
    @LogStar(title = "用户查询当前已处理流程", businessType = LogType.SELECT)
    public JsonResult findProcessed(Process process) {
        IPage<Process> page = processService.findProcessed(process);
        return JsonResult.successData(page);
    }

    @GetMapping("/findStarted")
    @LogStar(title = "用户查询当前已发起流程", businessType = LogType.SELECT)
    public JsonResult findStarted(Process process) {
        IPage<Process> page = processService.findStarted(process);
        return JsonResult.successData(page);
    }

    @GetMapping("/show/{id}")
    @LogStar(title = "根据流程id查询单个流程详情", businessType = LogType.SELECT)
    public JsonResult show(@PathVariable Long id) {
        Map<String, Object> result = processService.show(id);
        return JsonResult.successData(result);
    }

    @PostMapping("/approve")
    @LogStar(title = "开始审批", businessType = LogType.UPDATE)
    public JsonResult approve(@RequestBody ApprovalVo vo) {
        if(vo.getStatus().equals(AGREE.getCode())
                || vo.getStatus().equals(REJECT.getCode())) {
            processService.approve(vo);
            return JsonResult.success("操作成功");
        } else {
            throw new BadRequestException("流程状态错误");
        }
    }
}
