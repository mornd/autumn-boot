package com.mornd.system.entity.vo;

import com.mornd.system.entity.po.SysRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2022/1/5 - 9:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleVO extends SysRole {
    Set<String> perIds;
    @NotNull(message = "分页信息的起始页不能为空")
    private Integer pageNo;
    @NotNull(message = "分页信息的每页个数不能为空")
    private Integer pageSize;
}
