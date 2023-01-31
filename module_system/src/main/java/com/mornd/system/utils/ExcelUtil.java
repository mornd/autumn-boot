package com.mornd.system.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;

/**
 * @author: mornd
 * @dateTime: 2023/1/22 - 21:02
 * Excel 文件导出工具类
 */

@Slf4j
public class ExcelUtil {

    /**
     * 导出数据至excel
     * @param response 响应体
     * @param list 数据
     * @param clazz 数据类型
     * @param sheetName 工作表的名称
     */
    public static void export(HttpServletResponse response, Collection<?> list, Class<?> clazz, String sheetName) {
        if(list == null) {
            list = Collections.emptyList();
        }

        try {
            response.setContentType("application/vnd.ms-excel");

            response.setCharacterEncoding("utf-8");

            //String fileName = "";
            //fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");;
            //response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            response.addHeader("Access-Control-Expose-Headers", "Content-disposition");

            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(sheetName)
                    // 列宽
                    .registerWriteHandler(new SimpleColumnWidthStyleStrategy(18))
                    // 列高
                    .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 20, (short) 20))
                    .doWrite(list);
        } catch (IOException e) {
            log.info("导出文件失败");
        }
    }

    /**
     * 导出数据至excel
     * @param list 数据
     * @param clazz 数据类型
     * @param sheetName 工作表的名称
     */
    public static void export(Collection<?> list, Class<?> clazz, String sheetName) {
        export(ServletUtil.getResponse(), list, clazz, sheetName);
    }
}
