package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:53
 */
@Mapper
public interface PermissionMapper extends BaseMapper<SysPermission> {
    Set<SysPermission> getPersByRoleIds(@Param("roles") Set<String> roleIds, @Param("enabledState") Integer enabledState, @Param("excludeMenuType") Integer excludeMenuType, @Param("hidden") Integer hidden);

    Set<SysPermission> findCatalogueAndMenu(@Param("catalogue") Integer catalogue, @Param("menu") Integer menu);

    Set<SysPermission> findCatalogues(@Param("menu") Integer menu);
}
