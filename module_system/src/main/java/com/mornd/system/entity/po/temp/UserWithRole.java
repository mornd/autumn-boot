package com.mornd.system.entity.po.temp;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author mornd
 * @dateTime 2021/11/11 - 21:30
 * 用户角色关系表
 */
@Data
@TableName("sys_user_role")
public class UserWithRole implements Serializable {
    private static final long serialVersionUID = 1L;
    private String UserId;
    private String roleId;
    private Date gmtCreate;
}
