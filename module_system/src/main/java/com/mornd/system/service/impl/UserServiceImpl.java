package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.po.temp.UserWithRole;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysUserVO;
import com.mornd.system.mapper.UserMapper;
import com.mornd.system.mapper.UserWithRoleMapper;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:56
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {
    @Resource
    private UserWithRoleMapper userWithRoleMapper;
    @Resource
    private AuthUtil authUtil;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    private Integer enabled = BaseEntity.EnableState.ENABLE.getCode();
    private Integer disabled = BaseEntity.EnableState.DISABLE.getCode();

    /**
     * 验证当前密码
     * @param oldPwd
     * @return
     */
    @Override
    public boolean verifyCurrentPassword(String oldPwd) {
        //matches() => 参数1：明文，参数2：密文
        return passwordEncoder.matches(oldPwd, SecurityUtil.getEncryptionPassword());
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
            authUtil.delCacheLoginUser();
            return JsonResult.success(ResultMessage.UPDATE_MSG);
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
        
        //角色相关
        if(ObjectUtils.isNotEmpty(user.getRoles())) {
            user.getRoles().forEach(id -> {
                UserWithRole uw = new UserWithRole();
                uw.setUserId(sysUser.getId());
                uw.setRoleId(id);
                uw.setGmtCreate(new Date());
                userWithRoleMapper.insert(uw);
            });
        }
        return JsonResult.success("用户添加成功，用户的密码默认为：" + SecurityConst.USER_DEFAULT_PWD);
    }

    /**
     * 管理员修改用户信息
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

        //角色相关
        LambdaQueryWrapper<UserWithRole> qw = Wrappers.lambdaQuery();
        qw.eq(UserWithRole::getUserId, user.getId());
        userWithRoleMapper.delete(qw);
        if(ObjectUtils.isNotEmpty(user.getRoles())) {
            user.getRoles().forEach(id -> {
                UserWithRole uw = new UserWithRole();        
                uw.setUserId(sysUser.getId());
                uw.setRoleId(id);
                uw.setGmtCreate(new Date());
                userWithRoleMapper.insert(uw);
            });
        }
        authUtil.delCacheLoginUser();
        return JsonResult.success();
    }

    /**
     * 用户自己修改个人信息
     * @param user
     * @return
     */
    @Override
    public JsonResult userUpdate(SysUserVO user) {
        if(this.queryLoginNameExists(user.getLoginName(), user.getId())) return JsonResult.failure("登录名已重复");
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        qw.eq(SysUser::getId, user.getId());
        SysUser searchUser = baseMapper.selectOne(qw);
        //判断用户的登录名是否修改，如果用户修改了登录名则需重新登录
        boolean repeat = user.getLoginName().equals(searchUser.getLoginName()); 
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        sysUser.setStatus(null);
        sysUser.setGmtCreate(null);
        sysUser.setModifiedBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtModified(new Date());
        baseMapper.updateById(sysUser);
        authUtil.delCacheLoginUser();
        return JsonResult.success(ResultMessage.UPDATE_MSG, repeat);
    }
    
    /**
     * 用户修改头像
     * @param user
     * @return
     */
    @Override
    public JsonResult updateAvatar(SysUserVO user) {
        LambdaUpdateWrapper<SysUser> uw = Wrappers.lambdaUpdate();
        uw.eq(SysUser::getId, user.getId());
        uw.set(SysUser::getAvatar, user.getAvatar());
        baseMapper.update(null, uw);
        authUtil.delCacheLoginUser();
        return JsonResult.success("头像修改成功");
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
        authUtil.delCacheLoginUser();
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
        authUtil.delCacheLoginUser();
        return JsonResult.success(ResultMessage.UPDATE_MSG);
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

    @Override
    public JsonResult getRoleById(String id) {
        Set<String> ids = baseMapper.getRoleById(id);
        return JsonResult.successData(ids);
    }
}
