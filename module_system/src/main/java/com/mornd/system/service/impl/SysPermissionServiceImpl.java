package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.constant.GlobalConst;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.po.temp.RoleWithPermission;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.enums.EnumHiddenType;
import com.mornd.system.entity.enums.EnumPermissionType;
import com.mornd.system.mapper.RoleWithPermissionMapper;
import com.mornd.system.mapper.SysPermissionMapper;
import com.mornd.system.service.SysPermissionService;
import com.mornd.system.service.SysRoleService;
import com.mornd.system.utils.MenuUtil;
import com.mornd.system.utils.RedisUtil;
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
    @Resource
    private RedisUtil redisUtil;
    
    private Integer enabled = BaseEntity.EnableState.ENABLE.getCode();
    private Integer disabled = BaseEntity.EnableState.DISABLE.getCode();

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
        Set<SysPermission> sortPers = MenuUtil.toTree(GlobalConst.MENU_PARENT_ID, pers);
        return JsonResult.successData(sortPers);
    }

    /**
     * 页面的表格过滤筛选
     * @param sysPermission
     * @return
     */
    @Override
    public JsonResult filterTree(SysPermission sysPermission) {
        //查询所有菜单再过滤筛选符合条件的值
        Set<SysPermission> all = this.findByRoleIds(roleService.getCurrentRoleIds(), new SysPermission());
        if(ObjectUtils.isEmpty(all)) return JsonResult.successEmpty();
        //符合条件的数据
        //确定不能把按钮权限筛选掉
        sysPermission.setMenuType(null);
        Set<SysPermission> filterList = this.findByRoleIds(roleService.getCurrentRoleIds(), sysPermission);
        Set<SysPermission> result = MenuUtil.filterTree(GlobalConst.MENU_PARENT_ID, all, filterList);
        return JsonResult.successData(result);
    }

    /**
     * 只查询目录和菜单集合（排除按钮类型）
     * @return
     */
    @Override
    public JsonResult findCatalogueAndMenu() {
        List<String> currentRoleIds = roleService.getCurrentRoleIds();
        if(ObjectUtils.isEmpty(currentRoleIds)) {
            return JsonResult.successEmpty();
        }
        Set<SysPermission> pers = baseMapper.findCatalogueAndMenu(currentRoleIds, EnumPermissionType.CATALOGUE.getCode(), EnumPermissionType.MENU.getCode(), EnumHiddenType.DISPLAY.getCode());
        return JsonResult.successData(MenuUtil.toTree(GlobalConst.MENU_PARENT_ID, pers));
    }

    /**
     * 只查询目录集合（用于菜单新增或编辑时）
     * @return
     */
    @Override
    public JsonResult findCatalogues() {
        List<String> currentRoleIds = roleService.getCurrentRoleIds();
        if(ObjectUtils.isEmpty(currentRoleIds)) {
            return JsonResult.successEmpty();
        }
        Set<SysPermission> pers = baseMapper.findCatalogues(currentRoleIds, EnumPermissionType.CATALOGUE.getCode(), EnumHiddenType.DISPLAY.getCode());
        return JsonResult.successData(MenuUtil.toTree(GlobalConst.MENU_PARENT_ID, pers));
    }

    /**
     * 验证菜单标题是否重复(id为空是添加时验证，id有值是编辑时验证)
     * @param title
     * @param id
     * @return
     */
    @Override
    public boolean queryTitleRepeated(String title, String id) {
        LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
        qw.eq(SysPermission::getTitle, title);
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysPermission::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return count > 0;
    }

    /**
     * 验证菜单编码是否重复(id为空是添加时验证，id有值是编辑时验证)
     * @param code
     * @param id
     * @return
     */
    @Override
    public boolean queryCodeRepeated(String code, String id) {
        LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
        qw.eq(SysPermission::getCode, code);
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysPermission::getId, id);
        }
        Integer count = baseMapper.selectCount(qw);
        return count > 0;
    }

    /**
     * 添加操作
     * @param sysPermission
     * @return
     */
    @Override
    public JsonResult insert(SysPermission sysPermission) {
        if(queryTitleRepeated(sysPermission.getTitle(), null)) return JsonResult.failure("标题已存在");
        if(queryCodeRepeated(sysPermission.getCode(), null)) return JsonResult.failure("编码已存在");
        if(!GlobalConst.MENU_PARENT_ID.equals(sysPermission.getParentId())) {//如果菜单的父级不是是根节点
            //验证父级是否可用
            LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
            qw.eq(SysPermission::getId, sysPermission.getParentId());
            qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
            SysPermission parent = baseMapper.selectOne(qw);
            if(parent == null || disabled.equals(parent.getEnabled())) {
                return JsonResult.failure("操作失败，父级节点不存在或已被禁用");
            }
            //验证父级是否符合规范
            if(EnumPermissionType.CATALOGUE.getCode().equals(sysPermission.getMenuType())
                    || EnumPermissionType.MENU.getCode().equals(sysPermission.getMenuType())) {
                if(!parent.getMenuType().equals(EnumPermissionType.CATALOGUE.getCode())) {
                    return JsonResult.failure("目录或菜单的父级必须是目录类型");
                }
            }
            if(EnumPermissionType.BUTTON.getCode().equals(sysPermission.getMenuType())) {
                if(parent.getMenuType().equals(EnumPermissionType.BUTTON.getCode())) {
                    return JsonResult.failure("按钮类型的父级必须是目录或菜单类型");
                }
            }
        }
        sysPermission.setCreateBy(SecurityUtil.getLoginUserId());
        sysPermission.setGmtCreate(new Date());
        baseMapper.insert(sysPermission);
        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + SecurityUtil.getLoginUsername());
        return JsonResult.success();
    }

    /**
     * 编辑操作
     * @param sysPermission
     * @return
     */
    @Override
    public JsonResult update(SysPermission sysPermission) {
        if(queryTitleRepeated(sysPermission.getTitle(), sysPermission.getId())) return JsonResult.failure("标题已存在");
        if(queryCodeRepeated(sysPermission.getCode(), sysPermission.getId())) return JsonResult.failure("编码已存在");

        if(!GlobalConst.MENU_PARENT_ID.equals(sysPermission.getParentId())) {
            //验证父级是否符合规则
            LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
            qw.eq(SysPermission::getId, sysPermission.getParentId());
            qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
            SysPermission parent = baseMapper.selectOne(qw);
            if(parent == null || disabled.equals(parent.getEnabled())) {
                return JsonResult.failure("操作失败，父级节点不存在或已被禁用");
            }
            if(EnumPermissionType.CATALOGUE.getCode().equals(sysPermission.getMenuType())
                    || EnumPermissionType.MENU.getCode().equals(sysPermission.getMenuType())) {
                if(!parent.getMenuType().equals(EnumPermissionType.CATALOGUE.getCode())) {
                    return JsonResult.failure("目录或菜单的父级必须是目录类型");
                }
            }
            if(EnumPermissionType.BUTTON.getCode().equals(sysPermission.getMenuType())) {
                if(parent.getMenuType().equals(EnumPermissionType.BUTTON.getCode())) {
                    return JsonResult.failure("按钮类型的父级必须是目录或菜单类型");
                }
            }
        }
        //如果此节点为隐藏状态(并且该类型不是按钮类型)，则连同它的子节点都要隐藏
        if(EnumHiddenType.HIDDEN.getCode().equals(sysPermission.getHidden()) && !sysPermission.getMenuType().equals(EnumPermissionType.BUTTON.getCode())) {
            changeChildrenHidden(sysPermission.getId(), EnumHiddenType.HIDDEN.getCode());
        }
        //按钮类型和启用状态不可在这一步修改
        sysPermission.setMenuType(null);
        sysPermission.setEnabled(null);
        sysPermission.setGmtModified(new Date());
        sysPermission.setModifiedBy(SecurityUtil.getLoginUserId());
        baseMapper.updateById(sysPermission);
        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + SecurityUtil.getLoginUsername());
        return JsonResult.success();
    }

    /**
     * 更改（启用/禁用）状态
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
        if(disabled.equals(state)) {
            //如果修改为禁用状态则连同所有子节点一起禁用
            this.changeChildrenState(id, state);
        } else {
            //如果该节点的父级不是根节点，那么禁用时需再判断其父级的状态，如果父级是禁用状态则不可启用该节点
            LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
            qw.eq(SysPermission::getId, id);
            qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
            SysPermission per = baseMapper.selectOne(qw);
            if(per != null && !GlobalConst.MENU_PARENT_ID.equals(per.getParentId())) {
                qw = Wrappers.lambdaQuery();
                qw.eq(SysPermission::getId, per.getParentId());
                qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
                SysPermission parent = baseMapper.selectOne(qw);
                if(disabled.equals(parent.getEnabled())) {
                    return JsonResult.failure("请先启用父节点");
                }
            }
        }
        SysPermission per = new SysPermission();
        per.setId(id);
        per.setEnabled(state);
        baseMapper.updateById(per);
        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + SecurityUtil.getLoginUsername());
        return JsonResult.success("修改成功");
    }

    /**
     * （工具方法）递归修改子集的启用状态
     * @param id 要修改子集节点的id
     * @param state 修改后的状态
     */
    private void changeChildrenState(String id, Integer state) {
        LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
        qw.eq(SysPermission::getParentId, id);
        qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
        qw.ne(SysPermission::getEnabled, state);
        //查询当前节点的子集
        List<SysPermission> children = baseMapper.selectList(qw);
        if(!ObjectUtils.isEmpty(children)) {
            children.forEach(item -> {
                //执行修改
                LambdaUpdateWrapper<SysPermission> uw = Wrappers.lambdaUpdate();
                uw.set(SysPermission::getEnabled, state);
                uw.eq(SysPermission::getId, item.getId());
                baseMapper.update(null, uw);
                changeChildrenState(item.getId(), state);
            });
        }
    }

    /**
     * （工具方法）递归修改子集的显示隐藏
     * @param id 要修改子集节点的id
     * @param state 修改后的状态
     */
    private void changeChildrenHidden(String id, Integer state) {
        LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
        qw.eq(SysPermission::getParentId, id);
        qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
        qw.ne(SysPermission::getHidden, state);
        //查询当前节点的子集
        List<SysPermission> children = baseMapper.selectList(qw);
        if(!ObjectUtils.isEmpty(children)) {
            children.forEach(item -> {
                //执行修改
                LambdaUpdateWrapper<SysPermission> uw = Wrappers.lambdaUpdate();
                uw.set(SysPermission::getHidden, state);
                uw.eq(SysPermission::getId, item.getId());
                baseMapper.update(null, uw);
                changeChildrenState(item.getId(), state);
            });
        }
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
     * 删除菜单
     * @param id
     * @return
     */
    @Override
    public JsonResult delete(String id) {
        if(queryHasChildren(id)) {
            return JsonResult.failure("操作失败，该节点下已有子集，请先删除子集");
        }
        baseMapper.deleteById(id);
        LambdaQueryWrapper<RoleWithPermission> roleWithPerQw = Wrappers.lambdaQuery();
        roleWithPerQw.eq(RoleWithPermission::getPerId, id);
        roleWithPermissionMapper.delete(roleWithPerQw);
        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + SecurityUtil.getLoginUsername());
        return JsonResult.success();
    }
}
