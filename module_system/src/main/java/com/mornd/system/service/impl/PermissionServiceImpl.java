package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.constant.EntityConst;
import com.mornd.system.constant.GlobalConst;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.constant.enums.LoginUserSource;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.po.temp.RoleWithPermission;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.constant.enums.EnumHiddenType;
import com.mornd.system.constant.enums.EnumMenuType;
import com.mornd.system.mapper.RoleWithPermissionMapper;
import com.mornd.system.mapper.PermissionMapper;
import com.mornd.system.service.PermissionService;
import com.mornd.system.service.RoleService;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.MenuUtil;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:54
 */
@Service
@Transactional
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper,SysPermission> implements PermissionService {
    @Resource
    private RoleService roleService;
    @Resource
    private RoleWithPermissionMapper roleWithPermissionMapper;
    @Resource
    private AuthUtil authUtil;

    private Integer enabled = BaseEntity.EnableState.ENABLE.getCode();
    private Integer disabled = BaseEntity.EnableState.DISABLE.getCode();

    /**
     * 根据角色id集合获取对应的权限集合(不包括按钮权限类型)
     * @param ids 角色集合
     * @param excludeButton 是否排除按钮类型
     * @param enabledState 状态
     * @return
     */
    @Override
    public Set<SysPermission> getPersByRoleIds(List<String> ids, boolean excludeButton, Integer enabledState) {
        if(ObjectUtils.isEmpty(ids)) {
            return null;
        }
        return baseMapper.getPersByRoleIds(ids,
                enabledState,
                excludeButton ? EnumMenuType.BUTTON.getCode() : null,
                EnumHiddenType.DISPLAY.getCode());
    }

    /**
     * 展示页面左侧菜单树
     * @return
     */
    @Override
    public Set<SysPermission> leftTree() {
        SysUser loginUser = SecurityUtil.getLoginUser();
        Set<SysPermission> pers;
        if(LoginUserSource.LOCAL.getCode().equals(loginUser.getSource())) {
            pers = this.getPersByRoleIds(roleService.getCurrentRoleIds(), true, null);
        } else {
            // 非系统用户赋值一个默认角色
            LambdaQueryWrapper<SysRole> qw = Wrappers.lambdaQuery();
            qw.eq(SysRole::getCode, SecurityConst.GITEE_ROLE);
            qw.eq(SysRole::getEnabled, EntityConst.ENABLED);
            List<SysRole> roles = roleService.list(qw);
            pers = this.getPersByRoleIds(roles.stream().map(SysRole::getId).collect(Collectors.toList()),
                    true, null);
        }

        return MenuUtil.toTree(GlobalConst.MENU_PARENT_ID, pers);
    }

    /**
     * 查询页面表格数据(未去重、排序)
     * @return
     */
    @Override
    public List<SysPermission> tableTree() {
        LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
        qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
        List<SysPermission> pers = baseMapper.selectList(qw);
        return pers;
    }

    /**
     * 页面的表格过滤筛选
     * @param sysPermission
     * @return
     */
    @Override
    public JsonResult filterTableTree(SysPermission sysPermission) {
        //查询所有菜单再过滤筛选符合条件的值
        List<SysPermission> all = this.tableTree();
        //查询符合过滤条件的集合
        if(ObjectUtils.isEmpty(all)) {
            return JsonResult.successEmpty();
        }
        Set<SysPermission> filter = null;
        for (SysPermission per : all) {
            //是否包含搜索的关键字
            if(per.getTitle().contains(sysPermission.getTitle())) {
                if(filter == null) {
                    filter = new HashSet<>();
                }
                filter.add(per);
            }
        }
        Set<SysPermission> result = MenuUtil.filterTree(GlobalConst.MENU_PARENT_ID, new HashSet<>(all), filter);
        return JsonResult.successData(result);
    }

    /**
     * 只查询目录和菜单集合（排除按钮类型）
     * @return
     */
    @Override
    public JsonResult getCatalogueAndMenu() {
        Set<SysPermission> pers = baseMapper.findCatalogueAndMenu(EnumMenuType.CATALOGUE.getCode(), EnumMenuType.MENU.getCode(), EnumHiddenType.DISPLAY.getCode());
        return JsonResult.successData(MenuUtil.toTree(GlobalConst.MENU_PARENT_ID, pers));
    }

    /**
     * 只查询目录集合（用于菜单新增或编辑时）
     * @return
     */
    @Override
    public JsonResult getCatalogues() {
        Set<SysPermission> pers = baseMapper.findCatalogues(EnumMenuType.CATALOGUE.getCode(), EnumHiddenType.DISPLAY.getCode());
        return JsonResult.successData(MenuUtil.toTree(GlobalConst.MENU_PARENT_ID, pers));
    }

    /**
     * 查询所有显示的权限
     * @return
     */
    @Override
    public Set<SysPermission> getAllPers() {
        return baseMapper.getAllPers(EnumHiddenType.DISPLAY.getCode());
    }

    /**
     * 验证菜单标题是否重复(id为空是添加时验证，id有值是编辑时验证)
     * @param title
     * @param id
     * @return
     */
    @Override
    public boolean queryTitleExists(String title, String id) {
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
    public boolean queryCodeExists(String code, String id) {
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
        if(queryTitleExists(sysPermission.getTitle(), null)) return JsonResult.failure("标题已存在");
        if(queryCodeExists(sysPermission.getCode(), null)) return JsonResult.failure("编码已存在");
        if(sysPermission.getCode().startsWith(SecurityConst.ROLE_PREFIX)) {
            return JsonResult.failure("不可使用" + SecurityConst.ROLE_PREFIX + "作为权限编码的前缀");
        }

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
            if(EnumMenuType.CATALOGUE.getCode().equals(sysPermission.getMenuType())
                    || EnumMenuType.MENU.getCode().equals(sysPermission.getMenuType())) {
                if(!parent.getMenuType().equals(EnumMenuType.CATALOGUE.getCode())) {
                    return JsonResult.failure("目录或菜单的父级必须是目录类型");
                }
            }
            if(EnumMenuType.BUTTON.getCode().equals(sysPermission.getMenuType())) {
                if(parent.getMenuType().equals(EnumMenuType.BUTTON.getCode())) {
                    return JsonResult.failure("按钮类型的父级必须是目录或菜单类型");
                }
            }
        }
        sysPermission.setId(null);
        sysPermission.setCreateBy(SecurityUtil.getLoginUserId());
        sysPermission.setGmtCreate(new Date());
        baseMapper.insert(sysPermission);

        //超级管理员默认添加所有菜单权限
        RoleWithPermission rw = new RoleWithPermission();
        rw.setRoleId(SecurityConst.SUPER_ADMIN_ID);
        rw.setPerId(sysPermission.getId());
        rw.setGmtCreate(new Date());
        roleWithPermissionMapper.insert(rw);

        return JsonResult.success();
    }

    /**
     * 编辑操作
     * @param sysPermission
     * @return
     */
    @Override
    public JsonResult update(SysPermission sysPermission) {
        if(queryTitleExists(sysPermission.getTitle(), sysPermission.getId())) return JsonResult.failure("标题已存在");
        if(queryCodeExists(sysPermission.getCode(), sysPermission.getId())) return JsonResult.failure("编码已存在");
        if(sysPermission.getCode().startsWith(SecurityConst.ROLE_PREFIX)) {
            return JsonResult.failure("不可使用" + SecurityConst.ROLE_PREFIX + "作为权限编码的前缀");
        }
        if(!GlobalConst.MENU_PARENT_ID.equals(sysPermission.getParentId())) {
            //验证父级是否符合规则
            LambdaQueryWrapper<SysPermission> qw = Wrappers.lambdaQuery();
            qw.eq(SysPermission::getId, sysPermission.getParentId());
            qw.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
            SysPermission parent = baseMapper.selectOne(qw);
            if(parent == null || disabled.equals(parent.getEnabled())) {
                return JsonResult.failure("操作失败，父级节点不存在或已被禁用");
            }
            if(EnumMenuType.CATALOGUE.getCode().equals(sysPermission.getMenuType())
                    || EnumMenuType.MENU.getCode().equals(sysPermission.getMenuType())) {
                if(!parent.getMenuType().equals(EnumMenuType.CATALOGUE.getCode())) {
                    return JsonResult.failure("目录或菜单的父级必须是目录类型");
                }
            }
            if(EnumMenuType.BUTTON.getCode().equals(sysPermission.getMenuType())) {
                if(parent.getMenuType().equals(EnumMenuType.BUTTON.getCode())) {
                    return JsonResult.failure("按钮类型的父级必须是目录或菜单类型");
                }
            }
        }
        //如果此节点为隐藏状态(并且该类型不是按钮类型)，则连同它的子节点都要隐藏
        if(EnumHiddenType.HIDDEN.getCode().equals(sysPermission.getHidden()) && !sysPermission.getMenuType().equals(EnumMenuType.BUTTON.getCode())) {
            changeChildrenHiddenState(sysPermission.getId(), EnumHiddenType.HIDDEN.getCode());
        }
        //按钮类型和启用状态不可以在这一步修改
        sysPermission.setMenuType(null);
        sysPermission.setEnabled(null);
        sysPermission.setGmtModified(new Date());
        sysPermission.setModifiedBy(SecurityUtil.getLoginUserId());
        baseMapper.updateById(sysPermission);
        //authUtil.delCacheLoginUser();
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
            this.changeChildrenEnabledState(id, state);
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
//        authUtil.delCacheLoginUser();
        return JsonResult.success(ResultMessage.UPDATE_MSG);
    }

    /**
     * （工具方法）递归修改子集的启用状态
     * @param id 要修改子集节点的id
     * @param state 修改后的状态
     */
    private void changeChildrenEnabledState(String id, Integer state) {
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
                changeChildrenEnabledState(item.getId(), state);
            });
        }
    }

    /**
     * （工具方法）递归修改子集的显示状态
     * @param id 要修改子集节点的id
     * @param state 修改后的状态
     */
    private void changeChildrenHiddenState(String id, Integer state) {
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
                changeChildrenHiddenState(item.getId(), state);
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
        //authUtil.delCacheLoginUser();
        return JsonResult.success();
    }
}
