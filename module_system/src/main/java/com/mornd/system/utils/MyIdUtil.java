package com.mornd.system.utils;

import cn.hutool.core.util.IdUtil;

/**
 * @author: mornd
 * @dateTime: 2022/12/11 - 19:52
 * @description: uuid 工具类
 */
public class MyIdUtil {

    /**
     * 生成带-的随机数
     * adfe4f26-60ee-4ecf-92ea-ad49aa8da51d
     * @return
     */
    public static String fastUUID() {
        return IdUtil.fastUUID();
    }

    /**
     * 生成不带-的随机数
     * 7dffe0fcd67e45d381fba36aef89cddb
     * @return
     */
    public static String fastSimpleUUID() {
        return IdUtil.fastSimpleUUID();
    }
}
