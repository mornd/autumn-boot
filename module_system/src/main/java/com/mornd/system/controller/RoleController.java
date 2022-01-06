package com.mornd.system.controller;

import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysRoleVO;
import com.mornd.system.service.RoleService;
import com.mornd.system.validation.UpdateValidGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author mornd
 * @dateTime 2022/1/5 - 9:44
 */

@Api("角色接口")
@Validated
@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private RoleService roleService;

    @ApiOperation("分页查询")
    @GetMapping
    public JsonResult pageList(SysRoleVO role) {
        return roleService.pageList(role);
    }

    @ApiOperation("查询名称是否重复")
    @GetMapping("/queryNameRepeated")
    public JsonResult queryNameRepeated(@NotBlank(message = "名称不能为空") String name, String id) {
        return JsonResult.successData(roleService.queryNameRepeated(name, id));
    }

    @ApiOperation("查询编码是否重复")
    @GetMapping("/queryCodeRepeated")
    public JsonResult queryCodeRepeated(@NotBlank(message = "编码不能为空") String code, String id) {
        return JsonResult.successData(roleService.queryCodeRepeated(code, id));
    }
    
    @ApiOperation("添加角色")
    @PostMapping
    public JsonResult insert(@RequestBody @Validated SysRole role) {
        return roleService.insert(role);
    }

    @ApiOperation("修改角色")
    @PutMapping
    public JsonResult update(@RequestBody @Validated(UpdateValidGroup.class) SysRole role) {
        return roleService.update(role);
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable String id) {
        return roleService.delete(id);
    }
}
