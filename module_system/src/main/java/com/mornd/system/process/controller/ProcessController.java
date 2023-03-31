package com.mornd.system.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.controller.base.BaseController;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.process.entity.vo.ProcessVo;
import com.mornd.system.process.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
