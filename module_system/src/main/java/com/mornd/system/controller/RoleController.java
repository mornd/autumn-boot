package com.mornd.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.annotation.RepeatSubmit;
import com.mornd.system.constant.GlobalConst;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysRoleVO;
import com.mornd.system.service.PermissionService;
import com.mornd.system.service.RoleService;
import com.mornd.system.utils.MenuUtil;
import com.mornd.system.validation.BindValidGroup;
import com.mornd.system.validation.UpdateValidGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.Set;

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
    @Resource
    private PermissionService permissionService;

    @PreAuthorize("hasAnyAuthority('system:role')")
    @LogStar(value = "获取角色列表", businessType = LogType.SELECT)
    @ApiOperation("分页查询")
    @GetMapping
    public JsonResult pageList(SysRoleVO role) {
        IPage<SysRole> page = roleService.pageList(role);
        return JsonResult.successData(page);
    }

    @ApiOperation("查询名称是否重复")
    @GetMapping("/queryNameExists")
    public JsonResult queryNameExists(@NotBlank(message = "名称不能为空") String name, String id) {
        return JsonResult.successData(roleService.queryNameExists(name, id));
    }

    @ApiOperation("查询编码是否重复")
    @GetMapping("/queryCodeExists")
    public JsonResult queryCodeExists(@NotBlank(message = "编码不能为空") String code, String id) {
        return JsonResult.successData(roleService.queryCodeExists(code, id));
    }

    @PreAuthorize("hasAnyAuthority('system:role:add')")
    @RepeatSubmit
    @LogStar(value = "添加角色", businessType = LogType.INSERT)
    @ApiOperation("添加角色")
    @PostMapping
    public JsonResult insert(@RequestBody @Validated SysRole role) {
        roleService.insert(role);
        return JsonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('system:role:update')")
    @RepeatSubmit
    @LogStar(value = "修改角色", businessType = LogType.UPDATE)
    @ApiOperation("修改角色")
    @PutMapping
    public JsonResult update(@RequestBody @Validated(UpdateValidGroup.class) SysRole role) {
        roleService.update(role);
        return JsonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('system:role:delete')")
    @LogStar(value = "删除角色", businessType = LogType.DELETE)
    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable String id) {
        roleService.delete(id);
        return JsonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('system:role:update')")
    @LogStar(value = "更改角色状态", businessType = LogType.UPDATE)
    @ApiOperation("更改状态")
    @PutMapping("/changeState/{id}/{state}")
    public JsonResult changeStatus(@PathVariable(value = "id") @NotBlank(message = "id不能为空")
                                        String id,
                                   @PathVariable(value = "state") @Range(min = 0, max = 1, message = "修改的状态值不正确")
                                        Integer state) {
        roleService.changeStatus(id, state);
        return JsonResult.success(ResultMessage.UPDATE_MSG);
    }

    @ApiOperation("查询所有权限")
    @GetMapping("/getAllPers")
    public  JsonResult getAllPers() {
        Set<SysPermission> allPers = permissionService.getAllPers();
        return JsonResult.successData(MenuUtil.toTree(GlobalConst.MENU_PARENT_ID, allPers));
    }

    @PreAuthorize("hasAnyAuthority('system:role:update')")
    @ApiOperation("根据角色id查询所属的权限")
    @GetMapping("/getPersById/{id}")
    public JsonResult getPersById(@PathVariable String id) {
        Set<String> persById = roleService.getPersById(id);
        return JsonResult.successData(persById);
    }

    @PreAuthorize("hasAnyAuthority('system:role:update')")
    @LogStar(value = "给角色授权权限", businessType = LogType.INSERT)
    @ApiOperation("绑定角色对应的权限")
    @PutMapping("/bindPersById")
    public JsonResult bindPersById(@RequestBody @Validated(BindValidGroup.class) SysRoleVO role) {
        roleService.bindPersById(role.getId(), role.getPerIds());
        return JsonResult.success();
    }
}
