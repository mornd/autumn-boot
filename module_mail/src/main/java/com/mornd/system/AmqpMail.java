package com.mornd.system;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2023/2/1 - 20:23
 */
@Data
public class AmqpMail implements Serializable {
    private String username;
    private String loginName;
    private String mail;
    private LocalDateTime createdTime = LocalDateTime.now();
}
