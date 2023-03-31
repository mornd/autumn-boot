package com.mornd.system.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.process.entity.Process;
import com.mornd.system.process.entity.vo.ProcessVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: mornd
 * @dateTime: 2023/3/29 - 11:27
 */

@Mapper
public interface ProcessMapper extends BaseMapper<Process> {
    IPage<ProcessVo> pageList(@Param("page") IPage<ProcessVo> page, @Param("vo") ProcessVo vo);
}
