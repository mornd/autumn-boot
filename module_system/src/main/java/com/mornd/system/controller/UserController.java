package com.mornd.system.controller;

import com.mornd.system.annotation.LogStar;
import com.mornd.system.annotation.RepeatSubmit;
import com.mornd.system.entity.dto.ChangePwdDTO;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysUserVO;
import com.mornd.system.service.RoleService;
import com.mornd.system.service.UserService;
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

    @LogStar("获取当前登录用户信息")
    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/getLoginUser")
    public JsonResult getUserInfo(){
        SysUser loginUser = SecurityUtil.getLoginUser();
        loginUser.setPassword(null);
        return JsonResult.successData(loginUser);
    }

    @ApiOperation("匹配当前密码")
    @GetMapping("/verifyCurrentPassword")
    public JsonResult verifyCurrentPassword(@NotBlank(message = "当前密码不能为空") String oldPwd) {
        boolean matches = userService.verifyCurrentPassword(SecretUtil.desEncrypt(oldPwd));
        return JsonResult.successData(matches);
    }

    @LogStar("修改密码")
    @ApiOperation("修改密码")
    @PostMapping("/changePwd")
    public JsonResult changePwd(@RequestBody @Validated ChangePwdDTO pwd) {
        return userService.changePwd(SecretUtil.desEncrypt(pwd.getOldPwd()), SecretUtil.desEncrypt(pwd.getNewPwd()));
    }

    @PreAuthorize("hasAnyAuthority('system:user')") // any 代表拥有任意一个权限就可访问改接口
    @LogStar(value = "获取用户列表")
    @ApiOperation("获取用户表格数据")
    @GetMapping
    public JsonResult pageList(@Validated(SelectValidGroup.class) SysUserVO user) {
        return userService.pageList(user);
    }

    @PreAuthorize("hasAnyAuthority('system:user:add')")
    @RepeatSubmit
    @LogStar("新增用户")
    @ApiOperation("新增")
    @PostMapping
    public JsonResult insert(@RequestBody @Validated SysUserVO user) {
        return userService.insert(user);
    }

    @PreAuthorize("hasAnyRole('super_admin')")  // 拥有任意角色就可访问, "ROLE_" 的前缀可不加，看源码就知道, security 会补上前缀
    @RepeatSubmit
    @LogStar("管理员修改用户")
    @ApiOperation("修改")
    @PutMapping
    public JsonResult update(@RequestBody @Validated(UpdateValidGroup.class) SysUserVO user) {
        return userService.update(user);
    }

    @PreAuthorize("hasAnyAuthority('system:user:update')")
    @RepeatSubmit
    @LogStar("用户修改个人信息")
    @ApiOperation("用户个人修改信息")
    @PutMapping("/userUpdate")
    public JsonResult userUpdate(@RequestBody @Validated(UpdateValidGroup.class) SysUserVO user) {
        return userService.userUpdate(user);
    }

    @PreAuthorize("hasAnyAuthority('system:user:delete')")
    @LogStar("删除用户")
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable String id) {
        return userService.delete(id);
    }

    @PreAuthorize("hasAnyAuthority('system:user:changeStatus')")
    @LogStar("修改用户状态")
    @ApiOperation("更改状态")
    @GetMapping("/changeState")
    public JsonResult changeStatus(@NotBlank(message = "id不能为空") String id,
                                   @Range(min = 0, max = 1, message = "修改的状态值不正确") Integer state) {
        return userService.changeStatus(id, state);
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
        return userService.getRoleById(id);
    }

    @ApiOperation("用户授权角色时获取的所有角色集合(简单列)")
    @GetMapping("/getAllRoles")
    public JsonResult getAllRoles() {
        List<SysRole> roles = roleService.getAllRoles();
        return JsonResult.successData(roles);
    }
}
