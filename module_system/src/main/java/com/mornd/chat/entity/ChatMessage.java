package com.mornd.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: mornd
 * @dateTime: 2022/12/12 - 23:50
 * 聊天消息体
 */

@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = -1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;
}
