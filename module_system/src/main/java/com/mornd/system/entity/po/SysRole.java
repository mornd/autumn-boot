package com.mornd.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mornd.system.entity.po.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 10:20
 * 系统角色
 */
@Data
@TableName("sys_role")
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String code;
    private String name;
    private Boolean enabled;
    private String remark;

    //权限集合
    @TableField(exist = false)
    private Set<SysPermission> permissions;
}
