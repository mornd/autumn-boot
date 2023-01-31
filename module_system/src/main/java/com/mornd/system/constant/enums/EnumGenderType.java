package com.mornd.system.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: mornd
 * @dateTime: 2023/1/31 - 19:38
 */

@AllArgsConstructor
public enum EnumGenderType {
    /**
     * 男
     */
    MALE(1, "男"),
    /**
     * 女
     */
    FEMALE(0, "女");

    @Getter
    private final Integer code;
    @Getter
    private final String name;
}
