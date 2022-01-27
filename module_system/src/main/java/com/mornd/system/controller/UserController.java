package com.mornd.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.mornd.system.validation.ValidGroupA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
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

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/getLoginUser")
    public JsonResult getUserInfo(){
        return JsonResult.successData(SecurityUtil.getLoginUser());
    }

    @ApiOperation("匹配当前密码")
    @GetMapping("/verifyCurrentPassword")
    public JsonResult verifyCurrentPassword(@NotBlank(message = "当前密码不能为空") String oldPwd) {
        boolean matches = userService.verifyCurrentPassword(SecretUtil.desEncrypt(oldPwd));
        return JsonResult.successData(matches);
    }

    @ApiOperation("修改密码")
    @PostMapping("/changePwd")
    public JsonResult changePwd(@RequestBody @Validated ChangePwdDTO pwd) {
        return userService.changePwd(SecretUtil.desEncrypt(pwd.getOldPwd()), SecretUtil.desEncrypt(pwd.getNewPwd()));
    }
    
    @ApiOperation("获取用户表格数据")
    @GetMapping
    public JsonResult pageList(@Validated(SelectValidGroup.class) SysUserVO user) {
        return userService.pageList(user);
    }
    
    @ApiOperation("新增")
    @PostMapping
    public JsonResult insert(@RequestBody @Validated SysUserVO user) {
        return userService.insert(user);
    }
    
    @ApiOperation("修改")
    @PutMapping
    public JsonResult update(@RequestBody @Validated(UpdateValidGroup.class) SysUserVO user) {
        return userService.update(user);
    }

    @ApiOperation("用户个人修改信息")
    @PutMapping("/userUpdate")
    public JsonResult userUpdate(@RequestBody @Validated(UpdateValidGroup.class) SysUserVO user) {
        return userService.userUpdate(user);
    }

    @ApiOperation("用户修改头像")
    @PutMapping("/avatar")
    public JsonResult updateAvatar(@RequestBody @Validated(ValidGroupA.class) SysUserVO user) {
        return userService.updateAvatar(user);
    }
    
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable String id) {
        return userService.delete(id);
    }

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
