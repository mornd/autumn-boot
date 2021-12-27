package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.constants.GlobalConstants;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.type.EnumHiddenType;
import com.mornd.system.entity.type.EnumPermissionType;
import com.mornd.system.mapper.RoleWithPermissionMapper;
import com.mornd.system.mapper.SysPermissionMapper;
import com.mornd.system.service.SysPermissionService;
import com.mornd.system.service.SysRoleService;
import com.mornd.system.utils.MenuUtil;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:54
 */
@Service
@Transactional
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper,SysPermission> implements SysPermissionService {
    @Resource
    private SysRoleService roleService;
    @Resource
    private RoleWithPermissionMapper roleWithPermissionMapper;

    /**
     * 根据角色id查询权限
     * @param roles
     * @param sysPermission
     * @return
     */
    @Override
    public Set<SysPermission> findByRoleIds(List<String> roles, SysPermission sysPermission) {
        //默认查询非隐藏的菜单信息
        sysPermission.setHidden(EnumHiddenType.DISPLAY.getCode());
        if(ObjectUtils.isEmpty(roles)) return null;
        return baseMapper.findByRoleIds(roles, sysPermission);
    }

    /**
     * 展示页面左侧菜单树
     * @param filterPermission 是否筛选掉权限按钮
     * @return List || null
     */
    @Override
    public JsonResult getTree(boolean filterPermission) {
        List<String> currentRoleIds = roleService.getCurrentRoleIds();
        SysPermission sysPermission = new SysPermission();
        //排除按钮权限
        if(filterPermission) sysPermission.setMenuType(EnumPermissionType.BUTTON.getCode());
        Set<SysPermission> pers = this.findByRoleIds(currentRoleIds, sysPermission);
        Set<SysPermission> sortPers = MenuUtil.toTree(GlobalConstants.MENU_PARENT_ID, pers);
        return JsonResult.successData(sortPers);
    }

    /**
     * 过滤筛选
     * @param sysPermission
     * @return
     */
    @Override
    public JsonResult filterTree(SysPermission sysPermission) {
        //查询所有菜单
        Set<SysPermission> all = this.findByRoleIds(roleService.getCurrentRoleIds(), new SysPermission());
        if(ObjectUtils.isEmpty(all)) return JsonResult.successEmpty();
        //符合条件的数据
        //确定不能把按钮权限筛选掉
        sysPermission.setMenuType(null);
        Set<SysPermission> filterList = this.findByRoleIds(roleService.getCurrentRoleIds(), sysPermission);
        Set<SysPermission> result = MenuUtil.filterTree(GlobalConstants.MENU_PARENT_ID, all, filterList);
        return JsonResult.successData(result);
    }

    /**
     * 只查询目录和菜单（排除按钮类型）
     * @return
     */
    @Override
    public JsonResult findCatalogueAndMenu() {
        List<String> currentRoleIds = roleService.getCurrentRoleIds();
        if(ObjectUtils.isEmpty(currentRoleIds)) {
            return JsonResult.successEmpty();
        }
        Set<SysPermission> pers = baseMapper.findCatalogueAndMenu(currentRoleIds, EnumPermissionType.CATALOGUE.getCode(), EnumPermissionType.MENU.getCode(), EnumHiddenType.DISPLAY.getCode());
        return JsonResult.successData(MenuUtil.toTree(GlobalConstants.MENU_PARENT_ID, pers));
    }

    /**
     * 验证菜单标题是否重复(id为空是添加时验证，id有值是编辑时验证)
     * @param title
     * @param id
     * @return
     */
    @Override
    public JsonResult queryTitleRepeated(String title, String id) {
        LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
        qw.eq(SysPermission::getTitle, title);
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysPermission::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return JsonResult.successData(count > 0);
    }

    /**
     * 验证菜单编码是否重复(id为空是添加时验证，id有值是编辑时验证)
     * @param code
     * @param id
     * @return
     */
    @Override
    public JsonResult queryCodeRepeated(String code, String id) {
        LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
        qw.eq(SysPermission::getCode, code);
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysPermission::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return JsonResult.successData(count > 0);
    }

    @Override
    public JsonResult insert(SysPermission sysPermission) {
        //目录类型特殊处理
        if(sysPermission.getMenuType().equals(EnumPermissionType.CATALOGUE.getCode())) {
            sysPermission.setParentId(GlobalConstants.MENU_PARENT_ID);
        } else {
            //菜单、权限操作
            LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
            qw.eq(SysPermission::getId, sysPermission.getParentId());
            qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
            SysPermission per = baseMapper.selectOne(qw);
            if(per == null || BaseEntity.EnableState.DISABLE.getCode().equals(per.getEnabled())) {
                return JsonResult.failure("操作失败，父级节点不存在或已被禁用！");
            }
        }
        sysPermission.setId(null);
        sysPermission.setName(sysPermission.getTitle());
        sysPermission.setCreateBy(SecurityUtil.getLoginUserId());
        sysPermission.setGmtCreate(new Date());
        baseMapper.insert(sysPermission);
        return JsonResult.success("添加成功！");
    }

    /**
     * 查询该节点是否包含子节点
     * @param id
     * @return
     */
    @Override
    public boolean queryHasChildren(String id) {
        LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
        qw.eq(SysPermission::getParentId, id);
        qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
        Integer count = baseMapper.selectCount(qw);
        return count > 0;
    }

    /**
     * 编辑操作
     * @param sysPermission
     * @return
     */
    @Override
    public JsonResult update(SysPermission sysPermission) {
        if(BaseEntity.EnableState.DISABLE.getCode().equals(sysPermission.getEnabled())
            || EnumHiddenType.HIDDEN.getCode().equals(sysPermission.getHidden())) {
            //目录操作
            if(queryHasChildren(sysPermission.getId())) {
                return JsonResult.failure("操作失败，该节点以包含子集，不可禁用或隐藏！");
            }
        } else {
            //菜单、权限操作
            LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
            qw.eq(SysPermission::getId, sysPermission.getParentId());
            qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
            SysPermission per = baseMapper.selectOne(qw);
            if(per == null || BaseEntity.EnableState.DISABLE.getCode().equals(per.getEnabled())) {
                return JsonResult.failure("操作失败，父级节点不存在或已被禁用！");
            }
        }
        sysPermission.setName(sysPermission.getTitle());
        sysPermission.setGmtModified(new Date());
        sysPermission.setModifiedBy(SecurityUtil.getLoginUserId());
        baseMapper.updateById(sysPermission);
        return JsonResult.success("修改成功！");
    }

    @Override
    public JsonResult delete(String id) {
        if(queryHasChildren(id)) {
            return JsonResult.failure("操作失败，该节点下已有子集，请先删除子集！");
        }
        //baseMapper.delete(qw);
        /*LambdaQueryWrapper<RoleWithPermission> roleWithPerQw = Wrappers.lambdaQuery();
        roleWithPerQw.eq(RoleWithPermission::getPerId, id);
        roleWithPermissionMapper.delete(roleWithPerQw);*/
        return JsonResult.success("删除成功！");
    }
}
