package com.mornd.system.constant.enums;

/**
 * 日志类型枚举
 */
public enum LogType {
    SELECT(1, "查询"),
    INSERT(2, "新增"),
    UPDATE(3, "修改"),
    DELETE(4, "删除"),
    OTHER(5, "其他"),
    DOWNLOAD(6, "下载"),
    UPLOAD(7, "上传"),
    CLEAR(8, "清空表"),
    PUBLISH(9, "发布");

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
