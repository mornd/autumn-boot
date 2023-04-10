package com.mornd.system.entity.dto;

import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: mornd
 * @dateTime: 2023/4/10 - 10:29
 * 当前登录用户所拥有的权限
 */

@Data
public class SessionAuthority implements Serializable {

    public SessionAuthority() {
    }

    public SessionAuthority(SysRole role) {
        this.id = role.getId();
        this.name = role.getName();
        this.code = role.getCode();
    }

    public SessionAuthority(SysPermission permission) {
        this.id = permission.getId();
        this.name = permission.getTitle();
        this.code = permission.getCode();
    }

    private static final long serialVersionUID = -22345343L;

    private String id;
    private String name;
    private String code;
}
