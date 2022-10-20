package com.mornd.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.validation.UpdateValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 10:20
 * 系统用户
 */
@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper = true) //callSuper = true 会连同父类的属性加在一起生成equals(Object o)和hashCode()方法
@ApiModel("系统用户")
public class SysUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    //IdType.ASSIGN_UUID =>a794d53804816017978dba76b05b19a1
    // IdType.ASSIGN_ID =>1425004256210038785
    @TableId(type = IdType.ASSIGN_ID)
    @NotBlank(message = "ID不能为空",groups = {UpdateValidGroup.class})
    private String id;

    @NotBlank(message = "登录名不能为空")
    @ApiModelProperty("用户登录名称")
    private String loginName;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String  password;

    @ApiModelProperty("真实名称")
    private String realName;

    @ApiModelProperty("性别")
    private Integer gender;

    @ApiModelProperty("出生日期")
    private Date birthday;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("账号状态 0：禁用，1：正常")
    private Integer status;

    /*@ApiModelProperty("账号是否锁定 0：锁定 1：正常")
    private Integer accountLocked;*/

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("邮箱")
    private String email;

    //角色集合
    @TableField(exist = false)
    Set<SysRole> roles = new HashSet<>();

    //菜单权限集合
    @TableField(exist = false)
    Set<SysPermission> permissions = new HashSet<>();
}
