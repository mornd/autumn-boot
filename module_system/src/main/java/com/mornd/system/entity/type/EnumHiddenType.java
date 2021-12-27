package com.mornd.system.entity.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mornd
 * @dateTime 2021/11/8 - 10:05
 * 是否隐藏数据
 */
public enum EnumHiddenType {
    DISPLAY(1, "显示"),
    HIDDEN(0, "隐藏");

    @Getter
    private final Integer code;
    @Getter
    private final String name;

    EnumHiddenType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private static Map<Integer, EnumHiddenType> KEY_MAP_CODE = new HashMap<>();
    private static Map<String, EnumHiddenType> KEY_MAP_NAME = new HashMap<>();

    static {
        for (EnumHiddenType value : values()) {
            KEY_MAP_CODE.put(value.getCode(), value);
            KEY_MAP_NAME.put(value.getName(), value);
        }
    }

    public static EnumHiddenType getCode(Integer code) {
        return KEY_MAP_CODE.get(code);
    }

    public static EnumHiddenType getName(String name) {
        return KEY_MAP_NAME.get(name);
    }
}
