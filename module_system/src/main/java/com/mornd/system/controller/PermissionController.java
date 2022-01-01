package com.mornd.system.controller;

import com.mornd.system.constant.GlobalConstant;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.SysPermissionService;
import com.mornd.system.validation.UpdateValidGroup;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:55
 */
@Validated
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    private SysPermissionService permissionService;

    @ApiOperation("获取当前登录用户左侧菜单树")
    @GetMapping("/leftTree")
    public JsonResult getLeftTree(){
        return permissionService.getTree(true);
    }

    @ApiOperation("获取当前登录用户菜单管理的菜单表格")
    @GetMapping("/tableTree")
    public JsonResult getTableTree(){
        return permissionService.getTree(false);
    }

    @ApiOperation("根据条件筛选菜单数据")
    @GetMapping("/filterTree")
    public JsonResult filterTree(SysPermission sysPermission) {
        return permissionService.filterTree(sysPermission);
    }

    @ApiOperation("查询当前用户的目录和菜单集合")
    @GetMapping("/findCatalogueAndMenu")
    public JsonResult findCatalogueAndMenu() {
        return permissionService.findCatalogueAndMenu();
    }

    @ApiOperation("查询目录集合")
    @GetMapping("/findCatalogues")
    public JsonResult findCatalogues() {
        return permissionService.findCatalogues();
    }
    
    @ApiOperation("查询菜单标题是否重复(true:重复,false:不重复)")
    @GetMapping("/queryTitleRepeated")
    public JsonResult queryTitleRepeated(@NotBlank(message = "标题不能为空") String title, String id) {
        return JsonResult.successData(permissionService.queryTitleRepeated(title, id));
    }

    @ApiOperation("查询编码是否重复(true:重复,false:不重复)")
    @GetMapping("/queryCodeRepeated")
    public JsonResult queryCodeRepeated(@NotBlank(message = "标题不能为空") String code, String id) {
        return JsonResult.successData(permissionService.queryCodeRepeated(code, id));
    }

    @ApiOperation("查询目录是否包含子集")
    @GetMapping("/queryHasChildren/{id}")
    public JsonResult queryHasChildren(@PathVariable("id") String id) {
        try {
            return JsonResult.successData(permissionService.queryHasChildren(id));
        } catch (Exception e) {
            return JsonResult.failure("服务器异常，操作失败！");
        }
    }

    @ApiOperation("更改状态")
    @GetMapping("/changeState")
    public JsonResult changeStatus(@NotBlank(message = "id不能为空") String id, 
                                   @Range(min = 0, max = 1, message = "修改的状态值不正确") Integer state) {
        return permissionService.changeStatus(id, state);
    }

    @ApiOperation("新增菜单数据")
    @PostMapping("/")
    public JsonResult insert(@RequestBody @Validated SysPermission sysPermission) {
        return permissionService.insert(sysPermission);
    }

    @ApiOperation("编辑菜单数据")
    @PutMapping("/")
    public JsonResult update(@RequestBody @Validated(UpdateValidGroup.class) SysPermission sysPermission) {
        return permissionService.update(sysPermission);
    }

    @ApiOperation("删除操作")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable String id){
        return permissionService.delete(id);
    }
}
