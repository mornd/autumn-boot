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
        Integer menuType = sysPermission.getMenuType();
        if(menuType == null) return false;
        //验证菜单类型是否合法
        if(!menuType.equals(EnumPermissionType.CATALOGUE.getCode())
            && !menuType.equals(EnumPermissionType.MENU.getCode())
            && !menuType.equals(EnumPermissionType.BUTTON.getCode())) {
            return false;
        }
        if(menuType.equals(EnumPermissionType.CATALOGUE.getCode())) {
            //目录
            return !StringUtils.isBlank(sysPermission.getIcon());
        } else if(menuType.equals(EnumPermissionType.MENU.getCode())) {
            //菜单
            if(StringUtils.isBlank(sysPermission.getParentId())) return false;
            if(StringUtils.isBlank(sysPermission.getPath())) return false;
            if(StringUtils.isBlank(sysPermission.getIcon())) return false;
            return !StringUtils.isBlank(sysPermission.getComponent());
        } else {
            //权限
            return !StringUtils.isBlank(sysPermission.getParentId());
        }
    }
}
