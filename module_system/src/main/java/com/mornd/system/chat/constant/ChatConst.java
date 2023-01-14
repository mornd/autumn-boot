package com.mornd.system.chat.constant;

import lombok.Getter;

/**
 * @author: mornd
 * @dateTime: 2023/1/10 - 0:00
 * 常量枚举
 */
public enum ChatConst {
    UNREAD("未读", 0),
    READ("已读", 1),
    UNDELETE("未删除", 0),
    DELETE("已删除", 1);

    @Getter
    private String desc;
    @Getter
    private Integer value;

    private ChatConst(String desc, Integer value) {
        this.desc = desc;
        this.value = value;
    }
}
