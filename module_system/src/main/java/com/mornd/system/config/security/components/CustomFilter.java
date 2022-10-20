package com.mornd.system.config.security.components;

import com.mornd.system.utils.SecurityUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mornd
 * @dateTime 2022/10/18 - 21:18
 * 自定义过滤器，在 TokenAuthorizationFilter 过滤器之后执行
 */
@Component
public class CustomFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // todo
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
