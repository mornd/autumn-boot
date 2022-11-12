package com.mornd.system.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author mornd
 * @dateTime 2022/11/9 - 20:48
 * 异常打印工具类
 */
public class ExceptionUtil {
    public static String getMessage(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将错误的堆栈信息输出到 printWriter 中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if(sw != null) {
                try {
                    sw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
