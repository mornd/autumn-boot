package com.mornd.system.entity.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mornd
 * @dateTime 2021/11/14 - 12:11
 * 权限菜单类型
 */
public enum EnumPermissionType {
    CATALOGUE(0, "目录"),
    MENU(1, "菜单"),
    BUTTON(2, "按钮");

    @Getter
    private final Integer code;
    @Getter
    private final String name;

    EnumPermissionType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private static Map<Integer, EnumPermissionType> KEY_MAP_CODE = new HashMap<>();
    private static Map<String, EnumPermissionType> KEY_MAP_NAME = new HashMap<>();

    static {
        for (EnumPermissionType value : values()) {
            KEY_MAP_CODE.put(value.getCode(), value);
            KEY_MAP_NAME.put(value.getName(), value);
        }
    }

    public static EnumPermissionType getCode(Integer code) {
        return KEY_MAP_CODE.get(code);
    }

    public static EnumPermissionType getName(String name) {
        return KEY_MAP_NAME.get(name);
    }
}
