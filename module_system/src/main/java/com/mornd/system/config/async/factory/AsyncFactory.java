package com.mornd.system.config.async.factory;

import com.mornd.system.entity.po.SysLog;
import com.mornd.system.service.SysLogService;
import com.mornd.system.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 * 
 * @author ruoyi
 */
public class AsyncFactory
{
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 保存日志信息到数据库
     * @param sysLog
     * @return
     */
    public static TimerTask recordSysLogfor(final SysLog sysLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                SpringUtils.getBean(SysLogService.class).save(sysLog);
            }
        };
    }

}
