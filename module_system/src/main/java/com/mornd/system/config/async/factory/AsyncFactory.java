package com.mornd.system.config.async.factory;

import com.mornd.system.entity.po.SysLog;
import com.mornd.system.entity.po.SysLoginInfor;
import com.mornd.system.service.SysLogService;
import com.mornd.system.service.SysLoginInforService;
import com.mornd.system.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
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
     * 记录系统操作日志
     * @param sysLog
     * @return
     */
    public static TimerTask recordSysLog(final SysLog sysLog)
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

    /**
     * 记录登录日志
     * @param userId 用户id
     * @param loginName 登录名
     * @param type 登录方式
     * @param status 登录结果状态
     * @param msg 提示消息
     * @return
     */
    public static TimerTask recordSysLoginInfor(final String userId,
                                                final String loginName,
                                                final SysLoginInfor.Type type,
                                                final SysLoginInfor.Status status,
                                                final String msg) {
        HttpServletRequest request = ServletUtil.getRequest();
        String ip = IpUtils.getIpAddr(request);
        String address = AddressUtils.getRealAddressByIP(ip);
        String os = NetUtil.getOs(request);
        String browser = NetUtil.getBrowser(request);
        return new TimerTask() {
            @Override
            public void run() {
                SysLoginInfor sysLoginInfor = new SysLoginInfor();
                sysLoginInfor.setLoginName(loginName);
                sysLoginInfor.setStatus(status.getCode());
                sysLoginInfor.setType(type.getCode());
                sysLoginInfor.setIp(ip);
                sysLoginInfor.setAddress(address);
                sysLoginInfor.setOs(os);
                sysLoginInfor.setBrowser(browser);
                sysLoginInfor.setMsg(msg);
                if(StringUtils.hasText(userId)) {
                    sysLoginInfor.setUserId(userId);
                }
                SpringUtils.getBean(SysLoginInforService.class).insert(sysLoginInfor);
            }
        };
    }
}
