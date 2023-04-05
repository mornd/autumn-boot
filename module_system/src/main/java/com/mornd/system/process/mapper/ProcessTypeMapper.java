package com.mornd.system.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.process.entity.ProcessType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: mornd
 * @dateTime: 2023/3/25 - 22:23
 */
@Mapper
public interface ProcessTypeMapper extends BaseMapper<ProcessType> {
    /**
     * 查询流程类型及所属的模板(分页)
     * @param page
     * @param processType
     * @return
     */
    IPage<ProcessType> findTypeList(@Param("page") IPage<ProcessType> page, @Param("processType") ProcessType processType);
}
