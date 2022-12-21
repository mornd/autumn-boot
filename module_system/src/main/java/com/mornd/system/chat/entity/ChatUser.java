package com.mornd.system.chat.entity;

import com.mornd.system.entity.po.SysUser;
import lombok.Data;

/**
 * @author: mornd
 * @dateTime: 2022/12/13 - 19:23
 * 聊天用户实体
 */

@Data
public class ChatUser {

    /**
     * 将 sysUser 对象 构建为 chatUser 对象
     * @param sysUser
     */
    public ChatUser(SysUser sysUser) {
        this.id = sysUser.getId();
        this.loginName = sysUser.getLoginName();
        this.name = sysUser.getRealName();
        this.avatar = sysUser.getAvatar();
        this.gender = sysUser.getGender();
    }

    private String  id;
    private String loginName;
    private String name;
    private Integer gender;
    private String avatar;
}
