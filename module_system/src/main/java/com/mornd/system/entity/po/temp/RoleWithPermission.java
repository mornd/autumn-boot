package com.mornd.system.entity.po.temp;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author mornd
 * @dateTime 2021/11/11 - 21:32
 * 角色权限关系表
 */

@Data
@TableName("sys_role_permission")
public class RoleWithPermission implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roleId;
    private String perId;
    private Date gmtCreate;
}
