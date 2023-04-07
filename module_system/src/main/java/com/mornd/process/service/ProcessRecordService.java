package com.mornd.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.process.entity.ProcessRecord;

/**
 * @author: mornd
 * @dateTime: 2023/4/3 - 23:35
 */
public interface ProcessRecordService extends IService<ProcessRecord> {

    boolean insert(Long processId, Integer status, String description);
}
