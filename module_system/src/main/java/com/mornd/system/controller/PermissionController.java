package com.mornd.system.controller;

import com.mornd.system.annotation.LogStar;
import com.mornd.system.annotation.RepeatSubmit;
import com.mornd.system.constant.GlobalConst;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.PermissionService;
import com.mornd.system.utils.MenuUtil;
import com.mornd.system.validation.UpdateValidGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:55
 */
@Api("菜单权限接口")
@Validated
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @LogStar(value = "获取左侧菜单树", businessType = LogType.SELECT)
    @ApiOperation("获取当前登录用户左侧菜单树(包含启用、禁用状态)")
    @GetMapping("/leftTree")
    public JsonResult getLeftTree(){
        return JsonResult.successData(permissionService.leftTree());
    }

    @PreAuthorize("hasAnyAuthority('system:menu')")
    @LogStar(value = "获取菜单列表", businessType = LogType.SELECT)
    @ApiOperation("获取菜单管理的菜单表格")
    @GetMapping("/tableTree")
    public JsonResult tableTree(){
        List<SysPermission> pers = permissionService.tableTree();
        return JsonResult.successData(MenuUtil.toTree(GlobalConst.MENU_PARENT_ID, pers));
    }

    @ApiOperation("根据条件筛选菜单数据")
    @GetMapping("/filterTableTree")
    public JsonResult filterTableTree(SysPermission sysPermission) {
        Set<SysPermission> sysPermissions = permissionService.filterTableTree(sysPermission);
        return JsonResult.successData(sysPermissions);
    }

    @ApiOperation("查询目录和菜单集合")
    @GetMapping("/getCatalogueAndMenu")
    public JsonResult getCatalogueAndMenu() {
        Set<SysPermission> catalogueAndMenu = permissionService.getCatalogueAndMenu();
        return JsonResult.successData(catalogueAndMenu);
    }

    @ApiOperation("查询目录集合")
    @GetMapping("/getCatalogues")
    public JsonResult findCatalogues() {
        Set<SysPermission> catalogues = permissionService.getCatalogues();
        return JsonResult.successData(catalogues);
    }

    @ApiOperation("查询标题是否重复")
    @GetMapping("/queryTitleExists")
    public JsonResult queryTitleExists(@NotBlank(message = "标题不能为空") String title, String id) {
        return JsonResult.successData(permissionService.queryTitleExists(title, id));
    }

    @ApiOperation("查询编码是否重复")
    @GetMapping("/queryCodeExists")
    public JsonResult queryCodeExists(@NotBlank(message = "编码不能为空") String code, String id) {
        return JsonResult.successData(permissionService.queryCodeExists(code, id));
    }

    @ApiOperation("查询目录是否包含子集")
    @GetMapping("/queryHasChildren/{id}")
    public JsonResult queryHasChildren(@PathVariable("id") String id) {
        return JsonResult.successData(permissionService.queryHasChildren(id));
    }

    @PreAuthorize("hasAnyAuthority('system:menu:update')")
    @LogStar(value = "更改菜单状态", businessType = LogType.UPDATE)
    @ApiOperation("更改状态")
    @PutMapping("/changeState/{id}/{state}")
    public JsonResult changeStatus(@PathVariable(value = "id") @NotBlank(message = "id不能为空")
                                        String id,
                                   @PathVariable(value = "state") @Range(min = 0, max = 1, message = "修改的状态值不正确")
                                        Integer state) {
        permissionService.changeStatus(id, state);
        return JsonResult.success(ResultMessage.UPDATE_MSG);
    }

    @PreAuthorize("hasAnyAuthority('system:menu:add')")
    @RepeatSubmit
    @LogStar(value = "新增菜单", businessType = LogType.INSERT)
    @ApiOperation("新增菜单")
    @PostMapping
    public JsonResult insert(@RequestBody @Validated SysPermission sysPermission) {
        permissionService.insert(sysPermission);
        return JsonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('system:menu:update')")
    @RepeatSubmit
    @LogStar(value = "编辑菜单", businessType = LogType.UPDATE)
    @ApiOperation("编辑菜单")
    @PutMapping
    public JsonResult update(@RequestBody @Validated(UpdateValidGroup.class) SysPermission sysPermission) {
        permissionService.update(sysPermission);
        return JsonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('system:menu:delete')")
    @LogStar(value = "删除菜单", businessType = LogType.DELETE)
    @ApiOperation("删除菜单")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable String id){
        permissionService.delete(id);
        return JsonResult.success();
    }
}
