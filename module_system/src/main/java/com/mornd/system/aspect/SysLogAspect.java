package com.mornd.system.aspect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.entity.po.SysLog;
import com.mornd.system.service.SysLogService;
import com.mornd.system.utils.NetUtil;
import com.mornd.system.utils.SecurityUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/12/28 - 14:27
 * 日志切面
 */

@Aspect
@Component
@EnableAspectJAutoProxy
public class SysLogAspect {
    @Autowired
    private HttpServletRequest request;
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 切入点方法
     */
    @Pointcut("@annotation(com.mornd.system.annotation.LogStar)")
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
        try {
            long beginTime = System.currentTimeMillis();
            //执行目标方法
            result = pjp.proceed();
            time = System.currentTimeMillis() - beginTime;
        } catch (Throwable t) {
            //方法抛出异常
            throw (throwable = t);
        } finally {
            //处理日志
            handleSysLog(pjp, throwable, time, result);
        }
        return result;
    }

    private void handleSysLog(final JoinPoint joinPoint, final Throwable throwable, long processingTime, Object result) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //获取日志注解信息
        LogStar logStar = method.getAnnotation(LogStar.class);
        //标题
        String title = logStar.value();
        String url = request.getRequestURI();
        String ip = NetUtil.getIpAddress(request);

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
        sysLog.setUsername(SecurityUtil.getLoginUsername());
        sysLog.setMethodName(methodName);
        sysLog.setUrl(url);
        sysLog.setIp(ip);
        sysLog.setExecutionTime(processingTime);
        //操作系统及浏览器
        sysLog.setOsAndBrowser(NetUtil.getOsAndBrowserInfo(request));
        if(StrUtil.isNotBlank(params) && !"[]".equals(params)) {
            sysLog.setParams(params.length() > 500 ? params.substring(0, 500) + "——内容过长，以下内容已经忽略..." : params);    
        }
        
        //方法执行结果
        if (result != null) {
            String res = JSON.toJSONString(result);
            sysLog.setResult(res.length() > 500 ? res.substring(0, 500) + "——内容过长，以下内容已经忽略..." : res);
        }
        //访问时间
        sysLog.setVisitDate(new Date());
        //保存至数据库
        sysLogService.save(sysLog);
    }
}
