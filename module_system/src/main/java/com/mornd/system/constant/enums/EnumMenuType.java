package com.mornd.system.constant.enums;

import com.mornd.system.constant.EntityConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mornd
 * @dateTime 2021/11/14 - 12:11
 * 权限菜单类型
 */

@AllArgsConstructor
public enum EnumMenuType {
    CATALOGUE(0, "目录"),
    MENU(1, "菜单"),
    BUTTON(2, "按钮");

    @Getter
    private final Integer code;
    @Getter
    private final String name;
}
