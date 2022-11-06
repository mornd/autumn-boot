package com.mornd.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mornd.system.entity.po.SysLoginInfor;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysLoginInforVO;
import com.mornd.system.mapper.SysLoginInforMapper;
import com.mornd.system.service.SysLoginInforService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author mornd
 * @dateTime 2022/11/6 - 18:15
 */

@Service
public class SysLoginInforServiceImpl implements SysLoginInforService {
    @Resource
    private SysLoginInforMapper sysLoginInforMapper;

    /**
     * 列表查询
     * @param loginInforVO
     * @return
     */
    @Override
    public JsonResult<?> pageList(SysLoginInforVO loginInforVO) {
        IPage<SysLoginInfor> page = new Page<>(loginInforVO.getPageNo(), loginInforVO.getPageSize());
        LambdaQueryWrapper<SysLoginInfor> qw = Wrappers.lambdaQuery();
        qw.like(StringUtils.hasText(loginInforVO.getLoginName()), SysLoginInfor::getLoginName, loginInforVO.getLoginName());
        qw.eq(StringUtils.hasText(loginInforVO.getUserId()), SysLoginInfor::getUserId, loginInforVO.getUserId());
        if (Objects.nonNull(loginInforVO.getVisitDateScope()) && loginInforVO.getVisitDateScope().length == 2) {
            qw.between(SysLoginInfor::getLoginTime, loginInforVO.getVisitDateScope()[0], loginInforVO.getVisitDateScope()[1]);
        }
        qw.eq(Objects.nonNull(loginInforVO.getStatus()), SysLoginInfor::getStatus, loginInforVO.getStatus());
        qw.orderByDesc(SysLoginInfor::getLoginTime);
        page = sysLoginInforMapper.selectPage(page, qw);
        return JsonResult.successData(page);
    }

    /**
     * 保存记录
     * @param sysLoginInfor
     * @return
     */
    @Override
    public int insert(SysLoginInfor sysLoginInfor) {
        sysLoginInfor.setLoginTime(new Date());
        return sysLoginInforMapper.insert(sysLoginInfor);
    }

    /**
     * 截断表数据，清空所有
     */
    @Override
    public void truncate() {
        sysLoginInforMapper.truncate();
    }
}
