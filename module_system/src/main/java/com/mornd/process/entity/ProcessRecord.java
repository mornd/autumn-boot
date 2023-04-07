package com.mornd.process.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: mornd
 * @dateTime: 2023/4/3 - 23:31
 * 流程记录表
 */

@Data
@TableName(value = "oa_process_record")
public class ProcessRecord extends ProcessBaseEntity {
    /**
     * 流程id
     */
    private Long processId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 操作人id
     */
    private String operateUserId;

    /**
     * 操作人真实姓名
     */
    private String operateUser;
}
