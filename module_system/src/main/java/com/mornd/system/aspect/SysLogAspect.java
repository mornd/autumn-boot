package com.mornd.system.aspect;

import com.mornd.system.annotation.LogStar;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author mornd
 * @dateTime 2021/12/28 - 14:27
 * 日志切面
 */

//@Aspect
//@Component
//@EnableAspectJAutoProxy
public class SysLogAspect {
    @Autowired
    private HttpServletRequest request;

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
            //方法抛出移除
            throwable = t;
            throw t;
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
        if(method == null) return;
        //获取日志注解信息
        LogStar logStar = method.getAnnotation(LogStar.class);
        //获取request
        System.out.println(request);
        
    }

}
