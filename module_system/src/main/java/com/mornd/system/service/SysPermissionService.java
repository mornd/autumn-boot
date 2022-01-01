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
public interface SysPermissionService extends IService<SysPermission> {
    Set<SysPermission> findByRoleIds(List<String> roles, SysPermission sysPermission);

    JsonResult getTree(boolean filterPermission);

    JsonResult delete(String id);

    JsonResult filterTree(SysPermission sysPermission);

    JsonResult findCatalogueAndMenu();

    boolean queryTitleRepeated(String title, String id);

    boolean queryCodeRepeated(String code, String id);

    JsonResult insert(SysPermission sysPermission);

    JsonResult update(SysPermission sysPermission);

    boolean queryHasChildren(String id);

    JsonResult changeStatus(String id, Integer state);

    JsonResult findCatalogues();
}
