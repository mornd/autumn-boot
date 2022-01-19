package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.po.temp.UserWithRole;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysUserVO;
import com.mornd.system.mapper.UserMapper;
import com.mornd.system.mapper.UserWithRoleMapper;
import com.mornd.system.service.PermissionService;
import com.mornd.system.service.RoleService;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.RedisUtil;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:56
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private UserWithRoleMapper userWithRoleMapper;
    @Resource
    private RedisUtil redisUtil;
    @Value("${jwt.expiration}")
    private long expiration;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    private Integer enabled = BaseEntity.EnableState.ENABLE.getCode();
    private Integer disabled = BaseEntity.EnableState.DISABLE.getCode();

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
                Set<SysRole> roles = roleService.findByUserId(sysUser.getId());
                if(ObjectUtils.isNotEmpty(roles)) {
                    sysUser.setRoles(roles);
                    List<String> ids = new ArrayList<>();
                    roles.forEach(i -> ids.add(i.getId()));
                    Set<SysPermission> pers = permissionService.getPersByRoleIds(ids, enabled);
                    sysUser.setPermissions(pers);
                }
                redisUtil.setValue(RedisKey.CURRENT_USER_INFO_KEY + username,
                        sysUser,
                        expiration, TimeUnit.SECONDS);
            }
        }
        return sysUser;
    }

    /**
     * 验证当前密码
     * @param oldPwd
     * @return
     */
    @Override
    public boolean verifyCurrentPassword(String oldPwd) {
        //matches() => 参数1：明文，参数2：密文
        return passwordEncoder.matches(oldPwd, SecurityUtil.getPassword());
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @Override
    public JsonResult changePwd(String oldPwd, String newPwd) {
        if(verifyCurrentPassword(oldPwd)) {
            SysUser user = new SysUser();
            user.setId(SecurityUtil.getLoginUserId());
            //加密新密码
            user.setPassword(passwordEncoder.encode(newPwd));
            baseMapper.updateById(user);
            return JsonResult.success("修改成功");
        }
        return JsonResult.failure("原密码不匹配");
    }

    /**
     * 分页查询
     * @param user
     * @return
     */
    @Override
    public JsonResult pageList(SysUserVO user) {
        IPage<SysUserVO> page = new Page<>(user.getPageNo(), user.getPageSize());
        IPage<SysUserVO> userPage = baseMapper.pageList(page, user);
        return JsonResult.successData(userPage);
    }

    /**
     * 新增
     * @param user
     * @return
     */
    @Override
    public JsonResult insert(SysUserVO user) {
        if(this.queryLoginNameExists(user.getLoginName(), null)) return JsonResult.failure("登录名已重复");
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        sysUser.setId(null);
        //设置默认密码
        sysUser.setPassword(passwordEncoder.encode(SecurityConst.USER_DEFAULT_PWD));
        sysUser.setCreateBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtCreate(new Date());
        baseMapper.insert(sysUser);
        return JsonResult.success();
    }

    /**
     * 修改
     * @param user 
     * @return
     */
    @Override
    public JsonResult update(SysUserVO user) {
        if(this.queryLoginNameExists(user.getLoginName(), user.getId())) return JsonResult.failure("登录名已重复");
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        sysUser.setStatus(null);
        sysUser.setModifiedBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtModified(new Date());
        baseMapper.updateById(sysUser);
        return JsonResult.success();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public JsonResult delete(String id) {
        //删除关系表数据
        LambdaQueryWrapper<UserWithRole> qw = Wrappers.lambdaQuery();
        qw.eq(UserWithRole::getUserId, id);
        userWithRoleMapper.delete(qw);
        //执行删除
        baseMapper.deleteById(id);
        return JsonResult.success();
    }

    /**
     * 修改启用状态
     * @param id
     * @param state
     * @return
     */
    @Override
    public JsonResult changeStatus(String id, Integer state) {
        //参数校验
        if(!enabled.equals(state) && !disabled.equals(state)) {
            return JsonResult.failure("修改的状态值不正确");
        }
        LambdaUpdateWrapper<SysUser> uw = Wrappers.lambdaUpdate();
        uw.set(SysUser::getStatus, state);
        uw.eq(SysUser::getId, id);
        baseMapper.update(null, uw);
        return JsonResult.success("修改成功");
    }

    /**
     * 查询登录名是否重复
     * @param name
     * @param id
     * @return
     */
    @Override
    public boolean queryLoginNameExists(String name, String id) {
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        qw.eq(SysUser::getLoginName, name);
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysUser::getId, id);
        }
        int count = baseMapper.selectCount(qw);
        return count > 0;
    }
}
