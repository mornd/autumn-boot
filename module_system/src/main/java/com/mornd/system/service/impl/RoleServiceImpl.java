package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.entity.enums.EnumHiddenType;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.po.temp.RoleWithPermission;
import com.mornd.system.entity.po.temp.UserWithRole;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysRoleVO;
import com.mornd.system.mapper.RoleWithPermissionMapper;
import com.mornd.system.mapper.RoleMapper;
import com.mornd.system.mapper.UserWithRoleMapper;
import com.mornd.system.service.RoleService;
import com.mornd.system.utils.RedisUtil;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;


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
    private RedisUtil redisUtil;
    private Integer enabled = BaseEntity.EnableState.ENABLE.getCode();
    private Integer disabled = BaseEntity.EnableState.DISABLE.getCode();

    /**
     * 根据用户id查询其对应的角色信息
     * @param userId
     * @return
     */
    @Override
    public Set<SysRole> findByUserId(String userId) {
        return baseMapper.findByUserId(userId, enabled);
    }

    /**
     * 工具方法：获取当前用户的所有可用权限
     * @return
     */
    public Set<SysRole> getCurrentRoles() {
        return  findByUserId( SecurityUtil.getLoginUserId());
    }

    /**
     * 工具方法：获取当前用户的角色id集合
     * @return
     */
    public List<String> getCurrentRoleIds() {
        Set<SysRole> currentRoles = getCurrentRoles();
        if(!ObjectUtils.isEmpty(currentRoles)) {
            List<String> ids = new ArrayList<>();
            currentRoles.forEach(i -> ids.add(i.getId()));
            return ids;
        }
        return null;
    }

    /**
     * 分页查询
     * @param role
     * @return
     */
    @Override
    public JsonResult pageList(SysRoleVO role) {
        IPage<SysRole> page = new Page<>(role.getPageNo(),role.getPageSize());
        LambdaQueryWrapper<SysRole> qw = Wrappers.lambdaQuery();
        //筛选条件
        qw.like(StrUtil.isNotBlank(role.getName()), SysRole::getName, role.getName());
        qw.like(StrUtil.isNotBlank(role.getCode()), SysRole::getCode, role.getCode());
        qw.eq(!ObjectUtils.isEmpty(role.getEnabled()), SysRole::getEnabled, role.getEnabled());
        //排序
        qw.orderByAsc(SysRole::getSort);
        IPage<SysRole> pageResult = baseMapper.selectPage(page, qw);
        return JsonResult.successData(pageResult);
    }

    /**
     * 新增
     * @param role
     * @return
     */
    @Override
    public JsonResult insert(SysRole role) {
        if(queryNameExists(role.getName(), null)) return JsonResult.failure("名称已重复");
        if(queryCodeExists(role.getCode(), null)) return JsonResult.failure("编码已重复");
        role.setId(null);
        role.setCreateBy(SecurityUtil.getLoginUserId());
        role.setGmtCreate(new Date());
        baseMapper.insert(role);
        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + SecurityUtil.getLoginUsername());
        return JsonResult.success();
    }

    /**
     * 更新
     * @param role
     * @return
     */
    @Override
    public JsonResult update(SysRole role) {
        if(queryNameExists(role.getName(), role.getId())) return JsonResult.failure("名称已重复");
        if(queryCodeExists(role.getCode(), role.getId())) return JsonResult.failure("编码已重复");
        role.setModifiedBy(SecurityUtil.getLoginUserId());
        role.setEnabled(null);
        role.setGmtModified(new Date());
        baseMapper.updateById(role);
        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + SecurityUtil.getLoginUsername());
        return JsonResult.success();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public JsonResult delete(String id) {
        //解除其他关联关系
        this.deleteUserAssociated(id);
        this.deletePerAssociated(id);
        
        baseMapper.deleteById(id);
        userWithRoleMapper.deleteById(id);
        roleWithPermissionMapper.deleteById(id);
        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + SecurityUtil.getLoginUsername());
        return JsonResult.success();
    }

    /**
     * 获取角色对应的权限id集合
     * @param id
     * @return
     */
    @Override
    public JsonResult getPersById(String id) {
        Set<String> perIds = baseMapper.getPersById(id, EnumHiddenType.DISPLAY.getCode());
        return JsonResult.successData(perIds);
    }

    /**
     * 角色绑定权限
     * @param id
     * @param perIds
     * @return
     */
    @Override
    public JsonResult bindPersById(String id, Set<String> perIds) {
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
        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + SecurityUtil.getLoginUsername());
        return JsonResult.success();
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
        if(StrUtil.isNotBlank(id)) {
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
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysRole::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return count > 0;
    }
    
    /**
     * 修改状态值
     * @param id
     * @param state
     * @return
     */
    @Override
    public JsonResult changeStatus(String id, Integer state) {
        LambdaUpdateWrapper<SysRole> uw = Wrappers.lambdaUpdate();
        uw.set(SysRole::getEnabled, state);
        uw.eq(SysRole::getId, id);
        baseMapper.update(null, uw);
        return JsonResult.success("修改成功");
    }
}
