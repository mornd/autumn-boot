package com.mornd.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysRoleVO;

import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 16:27
 */
public interface RoleService extends IService<SysRole> {
    Set<SysRole> findByUserId(String userId, Integer enabled);

    Set<SysRole> getCurrentRoles();

    List<String> getCurrentRoleIds();


    JsonResult pageList(SysRoleVO role);

    JsonResult insert(SysRole role);

    JsonResult update(SysRole role);

    JsonResult delete(String id);
}
