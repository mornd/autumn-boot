package com.mornd.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.constant.EntityConst;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.constant.enums.EnumHiddenType;
import com.mornd.system.entity.dto.SessionAuthority;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.po.temp.RoleWithPermission;
import com.mornd.system.entity.po.temp.UserWithRole;
import com.mornd.system.entity.vo.SysRoleVO;
import com.mornd.system.exception.AutumnException;
import com.mornd.system.mapper.RoleWithPermissionMapper;
import com.mornd.system.mapper.RoleMapper;
import com.mornd.system.mapper.UserWithRoleMapper;
import com.mornd.system.service.PermissionService;
import com.mornd.system.service.RoleService;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.mornd.system.entity.po.base.BaseEntity.EnableState.DISABLE;
import static com.mornd.system.entity.po.base.BaseEntity.EnableState.ENABLE;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 16:28
 */
@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements RoleService {
    @Resource
    private RoleWithPermissionMapper roleWithPermissionMapper;
    @Resource
    private UserWithRoleMapper userWithRoleMapper;
    @Resource
    private PermissionService permissionService;
    @Resource
    private AuthUtil authUtil;

    /**
     * 根据用户id查询其对应的角色信息
     * @param userId
     * @return
     */
    @Override
    public List<SysRole> findByUserId(String userId) {
        return baseMapper.findByUserId(userId, ENABLE.getCode());
    }

    /**
     * 工具方法：获取当前用户的所有可用权限
     * @return
     */
    public List<SysRole> getCurrentRoles() {
        return findByUserId(SecurityUtil.getLoginUserId());
    }

    /**
     * 工具方法：获取当前用户的角色id集合
     * @return
     */
    public List<String> getCurrentRoleIds() {
        List<SysRole> currentRoles = getCurrentRoles();
        return currentRoles.stream().map(SysRole::getId).collect(Collectors.toList());
    }

    /**
     * 设置登录用户的角色、权限
     * @param sysUser
     */
    @Override
    public void setLoginUserPermissions(SysUser sysUser) {
        List<SysRole> roles = findByUserId(sysUser.getId());
        if(!roles.isEmpty()) {
            List<SessionAuthority> sessionRoles = roles.stream()
                            .map(SessionAuthority::new).collect(Collectors.toList());

            Set<SysPermission> pers = permissionService.getPersByRoleIds(roles.stream()
                            .map(SysRole::getId).collect(Collectors.toList()), false, EntityConst.ENABLED);

            // 添加角色集合
            sysUser.setRoles(sessionRoles);
            // 添加菜单权限集合
            sysUser.setPermissions(pers.stream()
                            .map(SessionAuthority::new)
                            .filter(p -> StringUtils.hasText(p.getCode())).collect(Collectors.toList()));
        }
    }

    /**
     * 分页查询
     * @param role
     * @return
     */
    @Override
    public IPage<SysRole> pageList(SysRoleVO role) {
        IPage<SysRole> page = new Page<>(role.getPageNo(),role.getPageSize());
        LambdaQueryWrapper<SysRole> qw = Wrappers.lambdaQuery();
        //筛选条件
        qw.like(StringUtils.hasText(role.getName()), SysRole::getName, role.getName());
        qw.like(StringUtils.hasText(role.getCode()), SysRole::getCode, role.getCode());
        qw.eq(role.getEnabled() != null, SysRole::getEnabled, role.getEnabled());
        //排序
        qw.orderByAsc(SysRole::getSort, SysRole::getGmtCreate);
        baseMapper.selectPage(page, qw);
        return page;
    }

    /**
     * 新增
     * @param role
     */
    @Override
    public void insert(SysRole role) {
        if(queryNameExists(role.getName(), null)) {
            throw new AutumnException("名称已重复");
        }
        if(queryCodeExists(role.getCode(), null)) {
            throw new AutumnException("编码已重复");
        }
        role.setId(null);
        role.setCreateBy(SecurityUtil.getLoginUserId());
        role.setGmtCreate(new Date());
        baseMapper.insert(role);
    }

    /**
     * 更新
     * @param role
     */
    @Override
    public void update(SysRole role) {
        if(SecurityConst.SUPER_ADMIN_ID.equals(role.getId())) {
            throw new AutumnException(ResultMessage.CRUD_SUPERADMIN);
        }
        if(queryNameExists(role.getName(), role.getId())) {
            throw new AutumnException("名称已重复");
        }
        if(queryCodeExists(role.getCode(), role.getId())) {
            throw new AutumnException("编码已重复");
        }
        role.setModifiedBy(SecurityUtil.getLoginUserId());
        role.setEnabled(null);
        role.setGmtModified(new Date());
        baseMapper.updateById(role);
        //authUtil.delCacheLoginUser();
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id) {
        if(SecurityConst.SUPER_ADMIN_ID.equals(id)) {
            throw new AutumnException(ResultMessage.CRUD_SUPERADMIN);
        }
        //解除其他关联关系
        this.deleteUserAssociated(id);
        this.deletePerAssociated(id);

        baseMapper.deleteById(id);
        userWithRoleMapper.deleteById(id);
        roleWithPermissionMapper.deleteById(id);
        //authUtil.delCacheLoginUser();
    }

    /**
     * 获取角色对应的权限id集合
     * @param id
     * @return
     */
    @Override
    public Set<String> getPersById(String id) {
        Set<String> perIds = baseMapper.getPersById(id, EnumHiddenType.DISPLAY.getCode());
        return perIds;
    }

    /**
     * 角色绑定权限
     * @param id
     * @param perIds
     */
    @Override
    public void bindPersById(String id, Set<String> perIds) {
        if(SecurityConst.SUPER_ADMIN_ID.equals(id)) {
            throw new AutumnException(ResultMessage.CRUD_SUPERADMIN);
        }
        //先删除所有关联的数据
        this.deletePerAssociated(id);
        if(!ObjectUtils.isEmpty(perIds)) {
            //绑定角色与权限之间的关系
            for (String updateId : perIds) {
                RoleWithPermission rwp = new RoleWithPermission();
                rwp.setRoleId(id);
                rwp.setPerId(updateId);
                rwp.setGmtCreate(new Date());
                roleWithPermissionMapper.insert(rwp);
            }
        }
        //authUtil.delCacheLoginUser();
    }

    /**
     * 删除角色时，解除与之对应的权限关系
     * @param id
     */
    private void deletePerAssociated(String id) {
        LambdaQueryWrapper<RoleWithPermission> qw = Wrappers.lambdaQuery();
        qw.eq(RoleWithPermission::getRoleId, id);
        roleWithPermissionMapper.delete(qw);
    }

    /**
     * 删除角色时，解除与之对应的用户关系
     * @param id
     */
    private void deleteUserAssociated(String id) {
        LambdaQueryWrapper<UserWithRole> qw = Wrappers.lambdaQuery();
        qw.eq(UserWithRole::getRoleId, id);
        userWithRoleMapper.delete(qw);
    }

    /**
     * 查询name是否重复
     * @param name
     * @param id
     * @return
     */
    @Override
    public boolean queryNameExists(String name, String id) {
        LambdaQueryWrapper<SysRole> qw = Wrappers.lambdaQuery();
        qw.eq(SysRole::getName, name);
        if(StringUtils.hasText(id)) {
            qw.ne(SysRole::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return count > 0;
    }

    /**
     * 查询code是否重复
     * @param code
     * @param id
     * @return
     */
    @Override
    public boolean queryCodeExists(String code, String id) {
        LambdaQueryWrapper<SysRole> qw = Wrappers.lambdaQuery();
        qw.eq(SysRole::getCode, code);
        if(StringUtils.hasText(id)) {
            qw.ne(SysRole::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return count > 0;
    }

    /**
     * 修改状态值
     * @param id
     * @param state
     */
    @Override
    public void changeStatus(String id, Integer state) {
        if(SecurityConst.SUPER_ADMIN_ID.equals(id)) {
            throw new AutumnException(ResultMessage.CRUD_SUPERADMIN);
        }
        //参数校验
        if(!ENABLE.getCode().equals(state) && !DISABLE.getCode().equals(state)) {
            throw new AutumnException("修改的状态值不正确");
        }
        LambdaUpdateWrapper<SysRole> uw = Wrappers.lambdaUpdate();
        uw.set(SysRole::getEnabled, state);
        uw.eq(SysRole::getId, id);
        baseMapper.update(null, uw);
    }

    /**
     * 获取用户授权时所需的所有角色清单
     * @return
     */
    @Override
    public List<SysRole> getAllRoles() {
        LambdaQueryWrapper<SysRole> qw = Wrappers.lambdaQuery(SysRole.class);
        qw.select(SysRole::getId, SysRole::getName, SysRole::getEnabled, SysRole::getSort);
        qw.orderByAsc(SysRole::getSort);
        return baseMapper.selectList(qw);
    }
}
