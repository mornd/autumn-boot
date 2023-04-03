package com.mornd.system.aspect;

import com.alibaba.fastjson.JSON;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.config.async.factory.AsyncFactory;
import com.mornd.system.config.async.manager.AsyncManager;
import com.mornd.system.entity.po.SysLog;
import com.mornd.system.utils.AddressUtils;
import com.mornd.system.utils.IpUtils;
import com.mornd.system.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
    @Resource
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
        Date visit = new Date();
        long time = 0;
        Throwable throwable = null;
        Object result = null;
        // 状态
        int status = SysLog.Status.SUCCESS.ordinal();
        try {
            long beginTime = System.currentTimeMillis();

            //执行业务目标方法
            result = pjp.proceed();

            time = System.currentTimeMillis() - beginTime;
            return result;
        } catch (Throwable t) {
            status = SysLog.Status.FAILURE.ordinal();
            //方法抛出异常
            throw (throwable = t);
        } finally {
            String username = SecurityUtil.getLoginUsername();
            String realName = SecurityUtil.getLoginUser().getRealName();
            //处理日志
            handleSysLog(pjp, username, realName, visit, time, status, throwable, result);
        }
    }

    /**
     * 日志记录入库
     * @param joinPoint
     * @param username
     * @param realName
     * @param visit
     * @param processingTime
     * @param status
     * @param throwable
     * @param result
     */
    private void handleSysLog(final JoinPoint joinPoint, String username, String realName, Date visit, long processingTime, int status, final Throwable throwable, Object result) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //获取日志注解信息
        LogStar logStar = method.getAnnotation(LogStar.class);
        //标题
        String title = logStar.value();
        if("".equals(title)) {
            title = logStar.title();
        }
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
        sysLog.setType(logStar.businessType().getCode());
        sysLog.setUsername(username);
        sysLog.setRealName(realName);
        sysLog.setMethodName(methodName);
        sysLog.setUrl(url);
        sysLog.setIp(IpUtils.getIpAddr(request));
        sysLog.setExecutionTime(processingTime);
        sysLog.setStatus(status);
        //访问时间
        sysLog.setVisitDate(visit);
        //操作系统及浏览器
        //sysLog.setOs(NetUtil.getOs(request));
        //sysLog.setBrowser(NetUtil.getBrowser(request));
        sysLog.setAddress(AddressUtils.getRealAddressByIP(sysLog.getIp()));
        // 报错记录长度限制
        final int restriction = 5000;
        if(logStar.isSaveRequestData()) {
            if(params != null) {
                sysLog.setParams(params.length() > restriction ? params.substring(0, restriction) + "——内容过长，以下内容已经忽略..." : params);
            }
        }
        if(logStar.isSaveResponseData()) {
            if(result != null) {
                //方法执行结果
                String res = JSON.toJSONString(result);
                sysLog.setResult(res.length() > restriction ? res.substring(0, restriction) + "——内容过长，以下内容已经忽略..." : res);
            }
        }

        //异常信息
        if (throwable != null) {
            String msg = throwable.getMessage();
            sysLog.setExceptionMsg(msg.length() > restriction ? msg.substring(0, restriction) + "——内容过长，以下内容已经忽略..." : msg);
        }

        // 异步保存至数据库
        AsyncManager.me().execute(AsyncFactory.recordSysLog(sysLog));
    }
}
