package com.mornd.system.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.process.entity.ProcessType;
import com.mornd.system.process.mapper.ProcessTypeMapper;
import com.mornd.system.process.service.ProcessTypeService;
import org.springframework.stereotype.Service;

/**
 * @author: mornd
 * @dateTime: 2023/3/25 - 22:24
 */

@Service
public class ProcessTypeServiceImpl
        extends ServiceImpl<ProcessTypeMapper, ProcessType>
        implements ProcessTypeService {

    /**
     * 实体对象
     * @param processType
     * @return true：已重复，不可在继续操作 false：未重复，可继续操作
     */
    @Override
    public boolean checkNameUnique(ProcessType processType) {
        LambdaQueryWrapper<ProcessType> qw = Wrappers.lambdaQuery(ProcessType.class);
        qw.eq(ProcessType::getName, processType.getName());
        if(processType.getId() != null) {
            // 修改
            qw.ne(ProcessType::getId, processType.getId());
        }
        // 添加
        return baseMapper.selectCount(qw) > 0;
    }
}
