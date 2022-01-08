package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.SysUser;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:55
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
