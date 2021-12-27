package com.mornd.system.validation.validator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.type.EnumPermissionType;
import com.mornd.system.validation.annotation.PermissionValidated;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author mornd
 * @dateTime 2021/11/20 - 14:11
 * 自定义校验实现类
 */
public class PermissionValidator implements ConstraintValidator<PermissionValidated, SysPermission> {

    @Override
    public boolean isValid(SysPermission sysPermission, ConstraintValidatorContext constraintValidatorContext) {
        if(sysPermission.getMenuType() == null) return false;
        if(sysPermission.getMenuType().equals(EnumPermissionType.CATALOGUE.getCode())) {
            return !StringUtils.isBlank(sysPermission.getIcon());
        } else if(sysPermission.getMenuType().equals(EnumPermissionType.MENU.getCode())) {
            if(StringUtils.isBlank(sysPermission.getParentId())) return false;
            if(StringUtils.isBlank(sysPermission.getPath())) return false;
            if(StringUtils.isBlank(sysPermission.getIcon())) return false;
            return !StringUtils.isBlank(sysPermission.getComponent());
        } else if(sysPermission.getMenuType().equals(EnumPermissionType.BUTTON.getCode())) {
            return !StringUtils.isBlank(sysPermission.getParentId());
        }
        return true;
    }
}
