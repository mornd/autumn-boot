package com.mornd.system.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2022/12/27 - 20:39
 */
@Data
@AllArgsConstructor
public class ChatSession implements Serializable {
    private static final long serialVersionUID = -1;

    public ChatSession(ChatRecord chatRecord, String self) {
        if(chatRecord.getChatMessage() != null) {
            this.content = chatRecord.getChatMessage().getContent();
        }
        this.date = chatRecord.getCreateTime();
        if(self.equals(chatRecord.getFromKey())) {
            this.self = true;
        }
    }

    private String content;
    private LocalDateTime date;
    private boolean self = false;
}
