package com.mornd.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mornd.system.validation.UpdateValidGroup;
import com.mornd.system.validation.PermissionValidated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("权限实体")
public class SysPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    @TableId(type = IdType.ASSIGN_ID)
    @NotBlank(message = "菜单ID不能为空", groups = UpdateValidGroup.class)
    private String id;

    @NotBlank(message = "菜单父级ID不能为空")
    @ApiModelProperty("父id")
    private String parentId;

    @NotBlank(message = "标题不能为空")
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("前端路由对象path值")
    private String path;

    @ApiModelProperty("前端路由对象component组件路径")
    private String component;

    @NotNull(message = "菜单权限编码不能为空")
    @ApiModelProperty("权限编码")
    private String code;

    @ApiModelProperty("图标")
    private String icon;

    @NotNull(message = "菜单排序不能为空")
    @ApiModelProperty("排序")
    private Double sort;

    @ApiModelProperty("是否保持激活(0：否 1：是)")
    private Integer keepAlive;

    @ApiModelProperty("是否要求权限")
    private Integer requireAuth;

    @NotNull(message = "菜单状态不能为空")
    @ApiModelProperty("是否启用(1：启用0：禁用)")
    private Integer enabled;

    @NotNull(message = "显隐状态不能为空")
    @ApiModelProperty("是否隐藏菜单(0：隐藏，1：显示)")
    private Integer hidden;

    @NotNull(message = "菜单类型不能为空")
    @ApiModelProperty("菜单类型(0:一级菜单;1:子菜单;2:按钮权限)")
    private Integer menuType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("创建人id")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("修改人id")
    private String modifiedBy;

    //子菜单
    @TableField(exist = false)
    private Set<SysPermission> children;
}
