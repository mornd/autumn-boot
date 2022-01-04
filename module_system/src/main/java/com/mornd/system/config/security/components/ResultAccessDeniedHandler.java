package com.mornd.system.config.security.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mornd.system.entity.result.JsonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author mornd
 * @dateTime 2021/9/6 - 12:36
 * 用户没有权限(403)访问时处理 403
 */
@Component
public class ResultAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        JsonResult<Object> jsonResult = JsonResult.failure("权限不足");
        jsonResult.setCode(HttpServletResponse.SC_FORBIDDEN);
        writer.write(new ObjectMapper().writeValueAsString(jsonResult));
        writer.flush();
        writer.close();
    }
}
