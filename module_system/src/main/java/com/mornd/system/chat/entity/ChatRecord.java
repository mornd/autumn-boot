package com.mornd.system.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fromKey;
    private String toKey;
    private LocalDateTime createTime;
    private Integer fromRead;
    private Integer toRead;
    private Integer fromDeleted;
    private Integer toDeleted;
    private Long messageId;
    private ChatMessage chatMessage;
}
