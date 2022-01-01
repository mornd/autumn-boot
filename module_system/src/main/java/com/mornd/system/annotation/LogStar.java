package com.mornd.system.annotation;

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
    String value() default "";
}
