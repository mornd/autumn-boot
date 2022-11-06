package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.SysLoginInfor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author mornd
 * @dateTime 2022/11/6 - 17:56
 */
@Mapper
public interface SysLoginInforMapper extends BaseMapper<SysLoginInfor> {

    @Update("TRUNCATE TABLE sys_logininfor")
    void truncate();
}
