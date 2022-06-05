package com.mornd.system.config.security.components;

import com.mornd.system.constant.ResultMessage;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.utils.RespUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:06
 * 用户没有权限访问时处理 403
 */
@Component
public class ResultAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        // 通过 response 写入数据，返回给前端
        RespUtil.writeResult(response, JsonResult.failure(HttpServletResponse.SC_FORBIDDEN,
                ResultMessage.NOT_AUTH));
    }
}
