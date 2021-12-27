package com.mornd.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.constants.RedisKey;
import com.mornd.system.constants.ResultMessage;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.mapper.SysUserMapper;
import com.mornd.system.service.SysPermissionService;
import com.mornd.system.service.SysRoleService;
import com.mornd.system.service.SysUserService;
import com.mornd.system.utils.RedisUtil;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:56
 */
@Service
@Transactional
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Resource
    private SysRoleService roleService;
    @Resource
    private SysPermissionService permissionService;
    @Resource
    private RedisUtil redisUtil;
    @Value("${jwt.expiration}")
    private long expiration;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 根据用户username查询所属用户、角色、权限信息
     * @param username
     * @return
     */
    @Override
    public SysUser findByUsername(String username) {
        SysUser sysUser;
        if(redisUtil.hasKey(RedisKey.CURRENT_USER_INFO_KEY + username)){
            sysUser = (SysUser) redisUtil.getValue(RedisKey.CURRENT_USER_INFO_KEY + username);
        }else{
            LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SysUser::getLoginName,username).last("LIMIT 1");
            sysUser = baseMapper.selectOne(queryWrapper);
            if(sysUser == null) {
                throw new UsernameNotFoundException(ResultMessage.USER_NOTFOUND);
            } else {
                //设置角色、权限
                Set<SysRole> roles = roleService.findByUserId(sysUser.getId(),
                        BaseEntity.EnableState.ENABLE.getCode());
                if(ObjectUtils.isNotEmpty(roles)) {
                    sysUser.setRoles(roles);
                    List<String> ids = new ArrayList<>();
                    roles.forEach(i -> ids.add(i.getId()));
                    SysPermission sysPermission = new SysPermission();
                    sysPermission.setEnabled(BaseEntity.EnableState.ENABLE.getCode());
                    sysUser.setPermissions(permissionService.findByRoleIds(ids, sysPermission));
                }
                redisUtil.setValue(RedisKey.CURRENT_USER_INFO_KEY + username,
                        sysUser,
                        expiration, TimeUnit.SECONDS);
            }
        }
        return sysUser;
    }

    @Override
    public boolean verifyCurrentPassword(String oldPwd) {
        //matches() => 参数1：明文，参数2：密文
        return passwordEncoder.matches(oldPwd, SecurityUtil.getPassword());
    }

    @Override
    public JsonResult changePwd(String oldPwd, String newPwd) {
        if(verifyCurrentPassword(oldPwd)) {
            SysUser user = new SysUser();
            user.setId(SecurityUtil.getLoginUserId());
            //加密新密码
            //user.setPassword(passwordEncoder.encode(newPwd));
            //int i = baseMapper.updateById(user);
            return JsonResult.success("修改成功");
        }
        return JsonResult.failure("原密码不匹配");
    }
}
