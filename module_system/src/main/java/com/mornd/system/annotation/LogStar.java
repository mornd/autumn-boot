package com.mornd.system.annotation;

import com.mornd.system.constant.enums.LogType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author mornd
 * @dateTime 2021/12/28 - 14:23
 * 标注此操作会生成日志的注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogStar {
    @AliasFor("title")
    String value() default "";
    //主题
    @AliasFor("value")
    String title() default "";
    //日志操作类型
    LogType BusinessType() default LogType.OTHER;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;
}
