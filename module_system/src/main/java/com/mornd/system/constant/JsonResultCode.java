package com.mornd.system.constant;

/**
 * @author mornd
 * @dateTime 2021/8/12 - 12:15
 * 返回状态码 code
 */
public interface JsonResultCode {
    //成功
    Integer SUCCESS_CODE = 200;

    //普通异常
    Integer COMMON_EXCEPTION = 500;

    //参数校验异常
    Integer VIOLATION_EXCEPTION = 600;
}
