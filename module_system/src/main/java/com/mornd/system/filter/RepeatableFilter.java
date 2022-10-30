package com.mornd.system.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

/**
 * Repeatable 过滤器
 * 该过滤器的作用只是将 request 增强，防重复逻辑判断在拦截器中实现
 * @author ruoyi
 */
public class RepeatableFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest
                && StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE))
        {
            // 偷梁换柱，将 request 换成可重复读取 inputStream 的 request
            request = new RepeatedlyRequestWrapper((HttpServletRequest) request, response);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy()
    {
    }
}
