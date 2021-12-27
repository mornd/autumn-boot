package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.temp.RoleWithPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mornd
 * @dateTime 2021/11/11 - 21:34
 */

@Mapper
public interface RoleWithPermissionMapper extends BaseMapper<RoleWithPermission> {
}
