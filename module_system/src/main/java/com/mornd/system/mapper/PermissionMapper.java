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
    Set<SysPermission> getPersByRoleIds(@Param("roles") List<String> currentRoleIds, @Param("enabledState") Integer enabledState, @Param("excludeMenuType") Integer excludeMenuType, @Param("hidden") Integer hidden);

    Set<SysPermission> findCatalogueAndMenu(@Param("catalogue") Integer catalogue, @Param("menu") Integer menu, @Param("hidden") Integer hidden);

    Set<SysPermission> findCatalogues(@Param("menu") Integer menu, @Param("hidden") Integer hidden);

    Set<SysPermission> getAllPers(@Param("hidden") Integer hidden);
}
