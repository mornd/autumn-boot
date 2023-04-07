package com.mornd.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.process.mapper.ProcessRecordMapper;
import com.mornd.process.service.ProcessRecordService;
import com.mornd.system.entity.po.SysUser;
import com.mornd.process.entity.ProcessRecord;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2023/4/3 - 23:35
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessRecordServiceImpl
        extends ServiceImpl<ProcessRecordMapper, ProcessRecord>
        implements ProcessRecordService {

    @Override
    public boolean insert(Long processId, Integer status, String description) {
        SysUser loginUser = SecurityUtil.getLoginUser();
        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUserId(loginUser.getId());
        processRecord.setOperateUser(loginUser.getRealName());
        processRecord.setCreateTime(LocalDateTime.now());
        processRecord.setCreateId(loginUser.getId());
        return super.save(processRecord);
    }
}
