package com.mornd.chat.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2022/12/24 - 19:17
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TransmissionChatMessage {
    private static final long serialVersionUID = -1L;

    private String from;

    private String fromName;

    private String to;

    private String toName;

    private String content;

    private LocalDateTime date;

    private boolean success = true;
}
