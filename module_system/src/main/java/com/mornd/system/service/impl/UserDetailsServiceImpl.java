package com.mornd.system.service.impl;

import cn.hutool.core.collection.IterUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.system.constant.EntityConst;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.mapper.UserMapper;
import com.mornd.system.service.PermissionService;
import com.mornd.system.service.RoleService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:51
 * 用户登录逻辑
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;

    /**
     * 根据用户username查询所属用户、角色、权限信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUser::getLoginName,username).last("LIMIT 1");
        SysUser sysUser = userMapper.selectOne(queryWrapper);
        if(Objects.isNull(sysUser)) {
            //throw new UsernameNotFoundException(""); // 该异常不管 message 填什么，最后都会被security替换成"用户名或密码错误"
            throw new BadRequestException("账号不存在");
        } else {
            if(!EntityConst.ENABLED.equals(sysUser.getStatus())) {
                throw new BadRequestException("该账号已被禁用");
            }
            //设置角色、权限
            Set<SysRole> roles = roleService.findByUserId(sysUser.getId());
            if(IterUtil.isNotEmpty(roles)) {
                sysUser.setRoles(roles);
                List<String> ids = new ArrayList<>();
                roles.forEach(i -> ids.add(i.getId()));
                Set<SysPermission> pers = permissionService.getPersByRoleIds(ids, false, EntityConst.ENABLED);
                sysUser.setPermissions(pers);
            }
        }
        // 将数据库用户封装为 security 用户
        return new AuthUser(sysUser);
    }
}
