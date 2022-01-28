package com.mornd.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.entity.po.SysLog;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysLogVO;

/**
 * @author mornd
 * @dateTime 2022/1/28 - 10:27
 */
public interface SysLogService extends IService<SysLog> {
    JsonResult pageList(SysLogVO log);
}
