package com.mornd.system.constant.enums;

/**
 * 日志类型枚举
 */
public enum LogType {
    LOGIN(1, "登录"),
    LOGOUT(2, "退出"),
    SELECT(3, "查询"),
    INSERT(4, "新增"),
    UPDATE(5, "修改"),
    DELETE(6, "删除"),
    OTHER(7, "其他"),
    DOWNLOAD(8, "下载"),
    UPLOAD(9, "上传"),
    CLEAR(10, "清空表");

    private LogType(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    private final Integer code;
    private final String type;

    public String getType() {
        return this.type;
    }

    public Integer getCode() {
        return this.code;
    }
}
