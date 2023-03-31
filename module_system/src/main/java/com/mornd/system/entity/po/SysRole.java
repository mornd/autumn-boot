package com.mornd.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.validation.BindValidGroup;
import com.mornd.system.validation.UpdateValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 10:20
 * 系统角色
 */
@Data
@TableName("sys_role")
@EqualsAndHashCode(callSuper = true)
@ApiModel("角色实体")
public class SysRole extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @NotBlank(message = "角色ID不能为空",groups = {UpdateValidGroup.class, BindValidGroup.class})
    private String id;

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty("名称")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    @ApiModelProperty("编码")
    private String code;

    @NotNull(message = "角色状态不能为空")
    @ApiModelProperty("状态")
    private Integer enabled;

    @NotNull(message = "角色排序不能为空")
    @ApiModelProperty("排序")
    private Integer sort;

    @Size(max = 500, message = "备注信息过长")
    @ApiModelProperty("备注")
    private String remark;

    //权限集合
    @TableField(exist = false)
    private Set<SysPermission> permissions;
}
