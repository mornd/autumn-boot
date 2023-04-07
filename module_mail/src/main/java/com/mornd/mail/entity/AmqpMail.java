package com.mornd.mail.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: mornd
 * @dateTime: 2023/2/1 - 20:23
 * mq 用作消息传递的实体，且必须实现 Serializable 接口
 */
@Data
public class AmqpMail implements Serializable {
    private String msgId;
    private String  userId;
    private String username;
    private String loginName;
    private String mail;
    private Date createdTime;
}
