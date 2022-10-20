package com.mornd.system.utils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 20:24
 */
public class PageUtil {

    /**
     * 分页
     * @param list
     * @param num
     * @param size
     * @return
     * @param <T>
     */
    public static <T> List<T> pageInfo(List<T> list, Integer num, Integer size) {
        if(num == null || num < 1) num = 1;
        if(size == null || size < 1) size = 10;
        if(num == 1) {
            return list.stream().limit(size).collect(Collectors.toList());
        } else {
            return list.stream().skip((long) (num - 1) * size)
                    .limit(num).collect(Collectors.toList());
        }
    }
}
