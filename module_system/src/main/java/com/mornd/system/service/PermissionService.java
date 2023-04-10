package com.mornd.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.entity.po.SysPermission;

import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:53
 */
public interface PermissionService extends IService<SysPermission> {
    Set<SysPermission> getPersByRoleIds(List<String> roleIds, boolean excludeButton, Integer enabledState);

    Set<SysPermission> leftTree();

    List<SysPermission> tableTree();

    Set<SysPermission> filterTableTree(SysPermission sysPermission);

    Set<SysPermission> getCatalogueAndMenu();

    Set<SysPermission> getCatalogues();

    void delete(String id);

    Set<SysPermission> getAllPers();

    boolean queryTitleExists(String title, String id);

    boolean queryCodeExists(String code, String id);

    void insert(SysPermission sysPermission);

    void update(SysPermission sysPermission);

    boolean queryHasChildren(String id);

    void changeStatus(String id, Integer state);
}
