package com.mornd.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.result.JsonResult;

import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:53
 */
public interface PermissionService extends IService<SysPermission> {
    Set<SysPermission> getPersByRoleIds(List<String> ids, Integer enabledState);

    Set<SysPermission> leftTree();

    List<SysPermission> tableTree();

    JsonResult filterTableTree(SysPermission sysPermission);

    JsonResult getCatalogueAndMenu();

    JsonResult getCatalogues();

    JsonResult delete(String id);

    Set<SysPermission> findAllPers();

    boolean queryTitleExists(String title, String id);

    boolean queryCodeExists(String code, String id);

    JsonResult insert(SysPermission sysPermission);

    JsonResult update(SysPermission sysPermission);

    boolean queryHasChildren(String id);

    JsonResult changeStatus(String id, Integer state);
}
