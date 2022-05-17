package com.mornd.system.config.security.components;

import com.mornd.system.constant.ResultMessage;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.utils.RespUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:07
 * 当用户未登录、未携带 token 或 token 过期情况下访问资源时的处理 401
 */
@Component
public class ResultAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        // 通过 response 写入数据，返回给前端
        RespUtil.writeResult(response, JsonResult.failure(response.SC_UNAUTHORIZED,
                ResultMessage.NOT_LOGGED));
    }
}
