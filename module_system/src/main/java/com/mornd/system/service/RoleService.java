package com.mornd.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.vo.SysRoleVO;

import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 16:27
 */
public interface RoleService extends IService<SysRole> {
    List<SysRole> findByUserId(String userId);

    List<SysRole> getCurrentRoles();

    List<String> getCurrentRoleIds();

    IPage<SysRole> pageList(SysRoleVO role);

    void insert(SysRole role);

    void update(SysRole role);

    void delete(String id);

    boolean queryNameExists(String name, String id);

    boolean queryCodeExists(String code, String id);

    Set<String> getPersById(String id);

    void bindPersById(String id, Set<String> perIds);

    void changeStatus(String id, Integer state);

    List<SysRole> getAllRoles();

    void setLoginUserPermissions(SysUser sysUser);
}
