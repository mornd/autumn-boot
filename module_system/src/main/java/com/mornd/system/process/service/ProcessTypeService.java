package com.mornd.system.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.process.entity.ProcessType;

/**
 * @author: mornd
 * @dateTime: 2023/3/25 - 22:23
 */
public interface ProcessTypeService extends IService<ProcessType> {
    boolean checkNameUnique(ProcessType processType);

    IPage<ProcessType> findTypeList(ProcessType processType);
}
