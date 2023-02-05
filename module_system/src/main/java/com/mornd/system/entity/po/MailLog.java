package com.mornd.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2023/2/4 - 12:57
 * 邮件记录
 */

@Data
@TableName("mail_log")
public class MailLog {

    @TableId(type = IdType.NONE)
    private String msgId;
    private String userId;
    /**
     * 状态(0：消息投递中，1：投递成功，2：投递失败)
     */
    private Integer status;
    private String routeKey;
    private String exchange;
    private Integer tryCount;
    private LocalDateTime tryTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
