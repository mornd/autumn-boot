package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.vo.SysRoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 16:26
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    Set<SysRole> findByUserId(@Param("userId") String userId, @Param("enableState") Integer enableState);

    IPage<SysRole> pageList(@Param("page") IPage<SysRole> page, @Param("userId") String userId, @Param("role") SysRoleVO role);
}
