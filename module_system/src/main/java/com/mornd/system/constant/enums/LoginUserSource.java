package com.mornd.system.constant.enums;

import lombok.Getter;

/**
 * @author mornd
 * @dateTime 2022/10/22 - 20:17
 * 文件存储位置
 */
public enum LoginUserSource {
    LOCAL("0", "系统用户"),
    GITEE("gitee", "gitee用户");

    @Getter
    private final String code;
    @Getter
    private final String desc;

    private LoginUserSource(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
