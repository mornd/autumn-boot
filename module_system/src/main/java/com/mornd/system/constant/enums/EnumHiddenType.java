package com.mornd.system.constant.enums;

import com.mornd.system.constant.EntityConst;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author mornd
 * @dateTime 2021/11/8 - 10:05
 * 是否隐藏数据
 */
@AllArgsConstructor
public enum EnumHiddenType {
    DISPLAY(1, "显示"),
    HIDDEN(0, "隐藏");

    @Getter
    private final Integer code;
    @Getter
    private final String name;
    
}
