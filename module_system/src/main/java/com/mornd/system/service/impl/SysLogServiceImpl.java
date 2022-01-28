package com.mornd.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.entity.po.SysLog;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysLogVO;
import com.mornd.system.mapper.SysLogMapper;
import com.mornd.system.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mornd
 * @dateTime 2022/1/28 - 10:27
 */

@Slf4j
@Service
@Transactional
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public JsonResult pageList(SysLogVO log) {
        IPage<SysLog> page = new Page<>(log.getPageNo(), log.getPageSize());
        LambdaQueryWrapper<SysLog> qw = Wrappers.lambdaQuery();
        qw.like(StringUtils.isNotBlank(log.getTitle()), SysLog::getTitle, log.getTitle());
        qw.orderByDesc(SysLog::getVisitDate);
        page = baseMapper.selectPage(page, qw);
        return JsonResult.successData(page);
    }
}
