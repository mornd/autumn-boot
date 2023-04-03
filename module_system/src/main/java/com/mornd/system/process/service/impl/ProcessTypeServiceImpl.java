package com.mornd.system.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.process.entity.ProcessType;
import com.mornd.system.process.mapper.ProcessTypeMapper;
import com.mornd.system.process.service.ProcessTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: mornd
 * @dateTime: 2023/3/25 - 22:24
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessTypeServiceImpl
        extends ServiceImpl<ProcessTypeMapper, ProcessType>
        implements ProcessTypeService {

    @Override
    public IPage<ProcessType> findTypeList(ProcessType processType) {
        IPage<ProcessType> page = new Page<>(processType.getPageNo(), processType.getPageSize());
        baseMapper.findTypeList(page, processType);

        IPage<ProcessType> typeList = baseMapper.findTypeList(page, processType);
        return page;
    }

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
