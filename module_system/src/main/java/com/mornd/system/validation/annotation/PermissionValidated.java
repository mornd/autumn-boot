package com.mornd.system.validation.annotation;

import com.mornd.system.validation.validator.PermissionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author mornd
 * @dateTime 2021/11/20 - 14:04
 * 自定义权限校验
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = PermissionValidator.class)
public @interface PermissionValidated {
    //校验失败返回的信息
    String message() default "权限类型对应所需的参数校验错误";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
