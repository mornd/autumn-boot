package com.mornd.system.aspect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.config.async.factory.AsyncFactory;
import com.mornd.system.config.async.manager.AsyncManager;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.constant.enums.LoginUserSource;
import com.mornd.system.entity.po.SysLog;
import com.mornd.system.utils.AddressUtils;
import com.mornd.system.utils.IpUtils;
import com.mornd.system.utils.NetUtil;
import com.mornd.system.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author mornd
 * @dateTime 2021/12/28 - 14:27
 * 日志切面
 */

@Slf4j
@Aspect
@Component
@Order(value = 1)
@EnableAspectJAutoProxy
public class SysLogAspect {
    @Autowired
    private HttpServletRequest request;

    /**
     * 切入点方法
     */
    @Pointcut("@annotation(com.mornd.system.annotation.LogStar))")
    public void  pc() {
    }

    /**
     * 环绕通知生成日志
     * @param pjp
     */
    @Around("pc()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long time = 0;
        Throwable throwable = null;
        Object result = null;
        String username = null;
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //获取日志注解信息
        LogStar logStar = method.getAnnotation(LogStar.class);
        LogType logType = logStar.BusinessType();
        try {
            long beginTime = System.currentTimeMillis();

            //执行退出方法须在退出前获取用户信息，否则空指针异常
            if(LogType.LOGOUT.equals(logType)) {
                username = SecurityUtil.getLoginUsername();
                username = formatUserName(username);
            }
            //执行目标方法
            result = pjp.proceed();
            if(!LogType.LOGOUT.equals(logType)) {
                try {
                    username = SecurityUtil.getLoginUsername();
                    username = formatUserName(username);
                } catch (Exception e){
                    username = "用户名或密码错误的用户";
                }
            }
            time = System.currentTimeMillis() - beginTime;
        } catch (Throwable t) {
            //方法抛出异常
            throw (throwable = t);
        } finally {
            //处理日志
            handleSysLog(pjp, username, time, throwable, result);
        }
        return result;
    }

    private void handleSysLog(final JoinPoint joinPoint, String username, long processingTime, final Throwable throwable, Object result) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //获取日志注解信息
        LogStar logStar = method.getAnnotation(LogStar.class);
        //标题
        String title = logStar.value();
        String url = request.getRequestURI();

        //类名
        String declaringTypeName = signature.getDeclaringTypeName();
        //方法名
        String methodName = declaringTypeName + "." + signature.getName();

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            Object o = args[i];
            if(o instanceof ServletRequest || o instanceof ServletResponse || o instanceof MultipartFile){
                args[i] = o.toString();
            }
        }
        //方法参数
        String params = JSON.toJSONString(args);

        SysLog sysLog = new SysLog();
        sysLog.setTitle(title);
        //日志类型
        sysLog.setType(logStar.BusinessType().getCode());
        sysLog.setUsername(username);
        sysLog.setMethodName(methodName);
        sysLog.setUrl(url);
        sysLog.setIp(IpUtils.getIpAddr(request));
        sysLog.setExecutionTime(processingTime);
        //操作系统及浏览器
        sysLog.setOs(NetUtil.getOs(request));
        sysLog.setBrowser(NetUtil.getBrowser(request));
        sysLog.setAddress(AddressUtils.getRealAddressByIP(sysLog.getIp()));
        if(StrUtil.isNotBlank(params) && !"[]".equals(params)) {
            sysLog.setParams(params.length() > 1000 ? params.substring(0, 1000) + "——内容过长，以下内容已经忽略..." : params);
        }

        //方法执行结果
        if (result != null) {
            String res = JSON.toJSONString(result);
            sysLog.setResult(res.length() > 1000 ? res.substring(0, 1000) + "——内容过长，以下内容已经忽略..." : res);
        }
        //异常信息
        if(throwable != null) {
            String msg = throwable.getMessage();
            if(!StringUtils.hasText(msg)) {
                msg = "抛出异常，无异常信息";
            }
            sysLog.setExceptionMsg(msg.length() > 1000 ? msg.substring(0, 1000) + "——内容过长，以下内容已经忽略..." : msg);
        }
        //访问时间
        sysLog.setVisitDate(new Date());
        // 异步保存至数据库
        AsyncManager.me().execute(AsyncFactory.recordSysLog(sysLog));
    }

    private String formatUserName(String username) {
        if(!LoginUserSource.LOCAL.getCode().equals(SecurityUtil.getLoginUser().getSource())) {
            return String.format("%s(%s)", username, SecurityUtil.getLoginUser().getSource());
        }
        return username;
    }
}

