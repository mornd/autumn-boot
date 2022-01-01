package com.mornd.system.config.security.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mornd.system.entity.result.JsonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author mornd
 * @dateTime 2021/9/6 - 12:28
 * 当用户未登录、未携带token或token过期情况下访问资源时的处理 401
 */
@Component
public class ResultAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        JsonResult<Object> jsonResult = JsonResult.failure("尚未登录，请先登录！");
        jsonResult.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        writer.write(new ObjectMapper().writeValueAsString(jsonResult));
        writer.flush();
        writer.close();
    }
}
