package com.mornd.system.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mornd.system.entity.result.JsonResult;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 18:43
 * response 工具类
 */
@Slf4j
public class RespUtil {
    /**
     * 响应结果集
     * @param response
     * @param jsonResult
     */
    public static void writeResult(HttpServletResponse response, JsonResult jsonResult) throws IOException {
        // 设置响应状态
        response.setStatus(jsonResult.getCode());
        // 防止响应数据中文乱码
        response.setCharacterEncoding("UTF-8");
        //response.setContentType("application/json;charset=utf-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(jsonResult));
        writer.flush();
        writer.close();
    }
}
