package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mornd
 * @dateTime 2022/1/7 - 17:58
 */
@Mapper
public interface LogMapper extends BaseMapper<SysLog> {
}
