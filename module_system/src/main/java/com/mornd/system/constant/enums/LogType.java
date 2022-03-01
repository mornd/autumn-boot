package com.mornd.system.constant.enums;

public enum LogType {
    LOGIN(1, "登录"),
    LOGOUT(2, "退出"),
    OTHER(3, "其他");

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
