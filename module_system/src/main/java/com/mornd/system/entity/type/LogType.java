package com.mornd.system.entity.type;

public enum LogType {
    SERVICE("业务日志"), SYSTEM("系统日志");

    LogType(String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return this.type;
    }





}
