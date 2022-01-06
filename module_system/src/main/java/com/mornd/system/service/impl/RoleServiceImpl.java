package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysRoleVO;
import com.mornd.system.mapper.RoleWithPermissionMapper;
import com.mornd.system.mapper.SysRoleMapper;
import com.mornd.system.mapper.UserWithRoleMapper;
import com.mornd.system.service.RoleService;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 16:28
 */
@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {
    @Resource
    private RoleWithPermissionMapper roleWithPermissionMapper;
    @Resource
    private UserWithRoleMapper userWithRoleMapper;
    private Integer enabled = BaseEntity.EnableState.ENABLE.getCode();
    private Integer disabled = BaseEntity.EnableState.DISABLE.getCode();
    
    @Override
    public Set<SysRole> findByUserId(String userId, Integer enabled) {
        return baseMapper.findByUserId(userId, enabled);
    }

    /**
     * 工具方法：获取当前用户的所有可用权限
     * @return
     */
    public Set<SysRole> getCurrentRoles() {
        return  findByUserId( SecurityUtil.getLoginUserId(), enabled);
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
        page = baseMapper.pageList(page, SecurityUtil.getLoginUserId(), role);
        return JsonResult.successData(page);
    }

    @Override
    public JsonResult insert(SysRole role) {
        role.setCreateBy(SecurityUtil.getLoginUserId());
        role.setGmtCreate(new Date());
        baseMapper.insert(role);
        return JsonResult.success();
    }

    @Override
    public JsonResult update(SysRole role) {
        role.setModifiedBy(SecurityUtil.getLoginUserId());
        role.setGmtModified(new Date());
        baseMapper.updateById(role);
        return JsonResult.success();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public JsonResult delete(String id) {
        baseMapper.deleteById(id);
        userWithRoleMapper.deleteById(id);
        roleWithPermissionMapper.deleteById(id);
        return JsonResult.success();
    }

    @Override
    public boolean queryNameRepeated(String name, String id) {
        LambdaQueryWrapper<SysRole> qw = Wrappers.lambdaQuery();
        qw.eq(SysRole::getName, name);
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysRole::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return count > 0;
    }

    @Override
    public boolean queryCodeRepeated(String code, String id) {
        LambdaQueryWrapper<SysRole> qw = Wrappers.lambdaQuery();
        qw.eq(SysRole::getCode, code);
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysRole::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return count > 0;
    }
}
