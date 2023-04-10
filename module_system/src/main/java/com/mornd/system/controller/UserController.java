package com.mornd.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.annotation.RepeatSubmit;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.entity.dto.ChangePwdDTO;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysUserVO;
import com.mornd.system.service.RoleService;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.ExcelUtil;
import com.mornd.system.utils.SecretUtil;
import com.mornd.system.utils.SecurityUtil;
import com.mornd.system.validation.SelectValidGroup;
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
 * @dateTime 2021/9/17 - 8:26
 */
@Validated
@RestController
@RequestMapping("/sysUser")
@Api("系统用户接口")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    @LogStar(value = "获取当前登录用户信息", businessType = LogType.SELECT)
    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/getLoginUser")
    public JsonResult getUserInfo(){
        SysUser loginUser = SecurityUtil.getLoginUser();
        loginUser.setPassword(null);
        loginUser.setOpenId(null);
        return JsonResult.successData(loginUser);
    }

    @ApiOperation("匹配当前密码")
    @GetMapping("/verifyCurrentPassword")
    public JsonResult verifyCurrentPassword(@NotBlank(message = "当前密码不能为空") String oldPwd) {
        boolean matches = userService.verifyCurrentPassword(SecretUtil.desEncrypt(oldPwd));
        return JsonResult.successData(matches);
    }

    @LogStar(value = "修改密码", businessType = LogType.UPDATE)
    @ApiOperation("修改密码")
    @PostMapping("/changePwd")
    public JsonResult changePwd(@RequestBody @Validated ChangePwdDTO pwd) {
        userService.changePwd(SecretUtil.desEncrypt(pwd.getOldPwd()), SecretUtil.desEncrypt(pwd.getNewPwd()));
        return JsonResult.success(ResultMessage.UPDATE_MSG);
    }

    @PreAuthorize("hasAnyAuthority('system:user')") // any 代表拥有任意一个权限就可访问改接口
    @LogStar(value = "获取用户列表", businessType = LogType.SELECT)
    @ApiOperation("获取用户表格数据")
    @GetMapping
    public JsonResult pageList(@Validated(SelectValidGroup.class) SysUserVO user) {
        IPage<SysUserVO> page = userService.pageList(user);
        return JsonResult.successData(page);
    }

    @PreAuthorize("hasAnyAuthority('system:user:add')")
    @RepeatSubmit
    @LogStar(value = "新增用户", businessType = LogType.INSERT)
    @ApiOperation("新增")
    @PostMapping
    public JsonResult insert(@RequestBody @Validated SysUserVO user) {
        userService.insert(user);
        return JsonResult.success("用户添加成功，密码为系统默认");
    }

    // hasAnyRole 用来判定有 ROLE_ 前缀的权限
    @PreAuthorize("hasAnyRole('super_admin')")  // 拥有其中任意一个角色就可访问,"ROLE_"的前缀可加可不加，看源码 security 会补上前缀
    @RepeatSubmit
    @LogStar(value = "管理员修改用户", businessType = LogType.UPDATE)
    @ApiOperation("修改")
    @PutMapping
    public JsonResult update(@RequestBody @Validated(UpdateValidGroup.class) SysUserVO user) {
//        if(SecurityConst.ROOT_USER_ID.equals(id)) {
//            return JsonResult.failure(ResultMessage.CRUD_ROOT_USER);
//        }
        userService.update(user);
        return JsonResult.success();
    }

    //@PreAuthorize("hasAnyAuthority('system:user:update')")
    @RepeatSubmit
    @LogStar(value = "用户修改个人信息", businessType = LogType.UPDATE)
    @ApiOperation("用户个人修改信息")
    @PutMapping("/userUpdate")
    public JsonResult userUpdate(@RequestBody @Validated(UpdateValidGroup.class) SysUserVO user) {
        userService.userUpdate(user);
        return JsonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('system:user:delete')")
    @LogStar(value = "删除用户", businessType = LogType.DELETE)
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable String id) {
        if(SecurityConst.ROOT_USER_ID.equals(id)) {
            return JsonResult.failure(ResultMessage.CRUD_ROOT_USER);
        }
        userService.delete(id);
        return JsonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('system:user:changeStatus')")
    @LogStar(value = "修改用户状态", businessType = LogType.UPDATE)
    @ApiOperation("更改状态")
    @PutMapping("/changeState/{id}/{state}")
    public JsonResult changeStatus(@PathVariable(value = "id") @NotBlank(message = "id不能为空")
                                        String id,
                                   @PathVariable(value = "state") @Range(min = 0, max = 1, message = "修改的状态值不正确")
                                        Integer state) {
        if(SecurityConst.ROOT_USER_ID.equals(id)) {
            return JsonResult.failure(ResultMessage.CRUD_ROOT_USER);
        }
        userService.changeStatus(id, state);
        return JsonResult.success(ResultMessage.UPDATE_MSG);
    }

    @ApiOperation("查询登录名是否重复")
    @GetMapping("/queryLoginNameExists")
    public JsonResult queryNameExists(@NotBlank(message = "名称不能为空") String name, String id) {
        return JsonResult.successData(userService.queryLoginNameExists(name, id));
    }

    @ApiOperation("查询手机号是否已被绑定")
    @GetMapping("/queryPhoneExists")
    public JsonResult<?> queryPhoneExists(@NotBlank(message = "手机号码不能为空") String phone, String id) {
        return JsonResult.successData(userService.queryPhoneExists(phone, id));
    }

    @ApiOperation("获取用户所拥有的角色id")
    @GetMapping("/getRoleById/{id}")
    public JsonResult getRoleById(@PathVariable String id) {
        Set<String> ids = userService.getRoleById(id);
        return JsonResult.successData(ids);
    }

    @ApiOperation("用户授权角色时获取的所有角色集合(简单列)")
    @GetMapping("/getAllRoles")
    public JsonResult getAllRoles() {
        List<SysRole> roles = roleService.getAllRoles();
        return JsonResult.successData(roles);
    }

    @LogStar(title = "导出用户excel", businessType = LogType.DOWNLOAD)
    @ApiOperation("导出excel")
    @PostMapping("/export")
    public void export(@RequestBody SysUserVO userVO) {
        List<SysUserVO> result = userService.export(userVO);
        ExcelUtil.export(result, SysUserVO.class, "系统用户");
    }
}
