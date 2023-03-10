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

@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleVO extends SysRole {
    Set<String> perIds;
    private Integer pageNo;
    private Integer pageSize;
}
