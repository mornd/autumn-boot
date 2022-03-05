package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author mornd
 * @dateTime 2022/1/7 - 17:58
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
    @Update("TRUNCATE TABLE SYS_LOG")
    void clearAll();
}
