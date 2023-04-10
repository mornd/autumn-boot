package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 16:26
 */
@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {
    List<SysRole> findByUserId(@Param("userId") String userId, @Param("enableState") Integer enableState);

    Set<String> getPersById(@Param("id") String id, @Param("hidden") Integer hidden);
}
