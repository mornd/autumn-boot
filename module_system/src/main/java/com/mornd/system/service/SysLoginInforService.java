package com.mornd.system.service;

import com.mornd.system.entity.po.SysLoginInfor;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysLoginInforVO;


/**
 * @author mornd
 * @dateTime 2022/11/6 - 18:14
 */
public interface SysLoginInforService {
    JsonResult<?> pageList(SysLoginInforVO loginInforVO);

    int insert(SysLoginInfor sysLoginInfor);

    void truncate();
}
