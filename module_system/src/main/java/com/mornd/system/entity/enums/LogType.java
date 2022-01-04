package com.mornd.system.entity.enums;

public enum LogType {
    SERVICE("业务日志"),
    SYSTEM("系统日志"),
    LOGIN("登录"),
    LOGOUT("退出");

    LogType(String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return this.type;
    }
}
