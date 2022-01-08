package com.mornd.system.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mornd
 * @dateTime 2021/11/14 - 12:11
 * 权限菜单类型
 */

@AllArgsConstructor
public enum EnumPermissionType {
    CATALOGUE(0, "目录"),
    MENU(1, "菜单"),
    BUTTON(2, "按钮");

    @Getter
    private final Integer code;
    @Getter
    private final String name;
}
