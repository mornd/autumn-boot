package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.vo.SysUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:55
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
    IPage<SysUserVO> pageList(@Param("page") IPage<SysUserVO> page, @Param("user") SysUserVO user);
}
