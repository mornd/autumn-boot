package com.mornd.chat.entity;

import com.mornd.system.entity.po.SysUser;
import lombok.Data;

import java.time.LocalDateTime;

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

    /**
     *  主键
     */
    private String  id;

    /**
     * 账号
     */
    private String loginName;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 最后聊天时间
     */
    private String lastMessage;

    /**
     * 最后聊天内容
     */
    private LocalDateTime lastDate;

    /**
     * 未读消息个数
     */
    private int unread = 0;
}
