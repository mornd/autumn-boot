package com.mornd.system.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2022/12/24 - 19:15
 * 聊天记录关系
 */

@Data
@TableName("chat_record")
public class ChatRecord {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 发件人账号
     */
    private String fromKey;
    /**
     * 收件人账号
     */
    private String toKey;
    /**
     * 发起时间
     */
    private LocalDateTime createTime;
    /**
     * 发件人是否读取( 0：未读1：已读)
     */
    private Integer fromRead;
    /**
     * 收件人是否读取
     */
    private Integer toRead;
    /**
     * 发件人是否删除该条消息(0：未删1：已删)
     */
    private Integer fromDeleted;
    /**
     * 收件人是否删除该条消息
     */
    private Integer toDeleted;
    /**
     * 消息id
     */
    private Long messageId;
    /**
     * 消息实体
     */
    @TableField(select = false, exist = false)
    private ChatMessage chatMessage;
}
