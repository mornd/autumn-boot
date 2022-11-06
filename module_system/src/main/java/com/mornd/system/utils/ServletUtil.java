package com.mornd.system.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author mornd
 * @dateTime 2022/11/6 - 18:16
 * servlet 工具类，可从中获取 request,response,session
 */
public class ServletUtil {
    public static ServletRequestAttributes getRequestAttributes()
    {
        // 默认子线程获取不到父线程的 request 对象，须在子线程执行之前添加：RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) requestAttributes;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession()
    {
        return getRequest().getSession();
    }
}
