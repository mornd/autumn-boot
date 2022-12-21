package com.mornd.system.chat.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2022/12/12 - 23:50
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = -1L;

    private String from;

    private String to;

    private String content;

    private LocalDateTime date;

    private String fromName;
}
