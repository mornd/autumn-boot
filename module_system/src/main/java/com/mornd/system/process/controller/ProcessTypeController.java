package com.mornd.system.process.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.controller.base.BaseController;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.process.entity.ProcessType;
import com.mornd.system.process.service.ProcessTypeService;
import com.mornd.system.validation.UpdateValidGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/3/25 - 22:25
 */

@RestController
@RequestMapping("/processType")
@RequiredArgsConstructor
public class ProcessTypeController extends BaseController {
    private final ProcessTypeService processTypeService;

    @GetMapping
    @LogStar(title = "查询审批类型列表", businessType = LogType.SELECT)
    public JsonResult pageList(ProcessType processType) {
        LambdaQueryWrapper<ProcessType> qw = Wrappers.lambdaQuery(ProcessType.class);
        qw.like(StringUtils.hasText(processType.getName()),
                ProcessType::getName, processType.getName());
        qw.orderByDesc(ProcessType::getId);
        IPage<ProcessType> page = new Page<>(processType.getPageNo(), processType.getPageSize());
        processTypeService.page(page, qw);
        return JsonResult.successData(page);
    }

    /**
     * 查询所有审批类型和其对应的模板集合
     * @return
     */
    @GetMapping("/findTypeAndTemplateList")
    public JsonResult findTypeAndTemplateList(ProcessType processType) {
        IPage<ProcessType> page =  processTypeService.findTypeList(processType);
        return JsonResult.successData(page);
    }

    @PostMapping
    @LogStar(title = "添加审批类型", businessType = LogType.INSERT)
    public JsonResult insert(@RequestBody @Validated ProcessType processType) {
        if (processTypeService.checkNameUnique(processType)) {
            return JsonResult.failure("名称已重复，请更换");
        }
        processType.setCreateId(getLoginUser().getId());
        processType.setCreateTime(LocalDateTime.now());
        boolean save = processTypeService.save(processType);
        return save ? success() : failure();
    }

    @PutMapping
    @LogStar(title = "修改审批类型", businessType = LogType.UPDATE)
    public JsonResult update(@RequestBody @Validated(UpdateValidGroup.class)
                                 ProcessType processType) {
        if (processTypeService.checkNameUnique(processType)) {
            return JsonResult.failure("名称已重复，请更换");
        }
        processType.setUpdateId(getLoginUser().getId());
        processType.setUpdateTime(LocalDateTime.now());
        boolean success = processTypeService.updateById(processType);
        return success ? success() : failure();
    }

    @DeleteMapping("/{id}")
    @LogStar(title = "删除审批类型", businessType = LogType.DELETE)
    public JsonResult delete(@PathVariable String id) {
        boolean row = processTypeService.removeById(id);
        return row ? success() : failure();
    }

    /**
     * 查询所有类型名称
     * @return
     */
    @GetMapping("/getAllTypeNames")
    public JsonResult getAllTypeNames() {
        LambdaQueryWrapper<ProcessType> qw = Wrappers.lambdaQuery(ProcessType.class);
        qw.select(ProcessType::getName, ProcessType::getId);
        qw.orderByDesc(ProcessType::getId);
        List<ProcessType> list = processTypeService.list(qw);
        return JsonResult.successData(list);
    }
}
