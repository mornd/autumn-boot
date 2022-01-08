package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:53
 */
@Mapper
public interface PermissionMapper extends BaseMapper<SysPermission> {
    Set<SysPermission> findByRoleIds(@Param("roles") List<String> roles, @Param("sysPermission") SysPermission sysPermission);

    Set<SysPermission> findCatalogueAndMenu(@Param("roles") List<String> currentRoleIds, @Param("catalogue") Integer catalogue, @Param("menu") Integer menu, @Param("hidden") Integer hidden);

    Set<SysPermission> findCatalogues(@Param("roles") List<String> currentRoleIds, @Param("menu") Integer menu, @Param("hidden") Integer hidden);

    Set<SysPermission> findAllPers(@Param("hidden") Integer hidden);
}
