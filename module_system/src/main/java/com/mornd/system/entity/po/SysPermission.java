package com.mornd.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mornd.system.validation.UpdateValidGroup;
import com.mornd.system.validation.annotation.PermissionValidated;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 16:44
 * 菜单权限
 */
@Data
@TableName("sys_permission")
@PermissionValidated
public class SysPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @TableId(type = IdType.ASSIGN_ID)
    @NotBlank(message = "菜单ID不能为空", groups = UpdateValidGroup.class)
    private String id;

    //父id
    private String parentId;

    //菜单标题
    @NotBlank(message = "标题不能为空")
    private String title;

    //前端路由对象name值
    private String name;

    //前端路由对象path值
    private String path;

    //前端路由对象component组件路径
    private String component;

    //权限编码
    @NotNull(message = "菜单权限编码不能为空")
    private String code;

    //图标
    private String icon;

    //排序
    @NotNull(message = "菜单排序不能为空")
    private Double sort;

    //是否保持激活(0：否 1：是)
    private Integer keepAlive;

    //是否要求权限
    private Integer requireAuth;

    //是否启用(1：启用0：禁用)
    @NotNull(message = "菜单状态不能为空")
    private Integer enabled;

    //是否隐藏菜单(0：隐藏，1：显示)
    @NotNull(message = "显隐状态不能为空")
    private Integer hidden;

    //菜单类型(0:一级菜单;1:子菜单;2:按钮权限)
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    //是否路由菜单: 0:不是  1:是（默认值1）
    private Integer isRoute;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gmtCreate;

    //创建人
    private String createBy;

    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gmtModified;

    //修改人
    private String modifiedBy;

    //子菜单
    @TableField(exist = false)
    private Set<SysPermission> children;
}
