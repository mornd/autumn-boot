package com.mornd.system.validation.validator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.constant.enums.EnumMenuType;
import com.mornd.system.validation.PermissionValidated;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author mornd
 * @dateTime 2021/11/20 - 14:11
 * 自定义校验实现类
 */
public class PermissionValidator implements ConstraintValidator<PermissionValidated, SysPermission> {

    @Override
    public boolean isValid(SysPermission sysPermission, ConstraintValidatorContext context) {
        // 获取默认的提示信息（注解中的message值）
        String messageTemplate = context.getDefaultConstraintMessageTemplate();
        // 禁用默认提示信息
        context.disableDefaultConstraintViolation();
        // 设置新的提示语
        context.buildConstraintViolationWithTemplate("权限类型对应所需的参数校验错误!").addConstraintViolation();

        Integer menuType = sysPermission.getMenuType();
        if(menuType == null) return false;
        //验证菜单类型是否合法
        if(!menuType.equals(EnumMenuType.CATALOGUE.getCode())
            && !menuType.equals(EnumMenuType.MENU.getCode())
            && !menuType.equals(EnumMenuType.BUTTON.getCode())) {
            return false;
        }
        if(menuType.equals(EnumMenuType.CATALOGUE.getCode())) {
            //目录
            return !StringUtils.isBlank(sysPermission.getIcon());
        } else if(menuType.equals(EnumMenuType.MENU.getCode())) {
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
