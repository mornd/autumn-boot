package com.mornd.system.process.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author: mornd
 * @dateTime: 2023/3/29 - 11:27
 */

@Data
@TableName("oa_process")
public class Process extends ProcessBaseEntity {

    /**
     * 审批编码
     */
    private String processCode;

    /**
     *  标题
     */
    private String title;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 状态(0:默认1:审批中2:审批通过-1:驳回)
     */
    private Integer status;

    /**
     * 审批模板id
     */
    private Long processTemplateId;

    /**
     * 审批类型id
     */
    private Long processTypeId;

    /**
     * 表单值
     */
    private String formValues;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 当前审批人
     */
    private String currentAuditor;

    /**
     * 描述
     */
    private String description;

    @Getter
    @AllArgsConstructor
    public enum Status {
        /**
         * 默认
         */
        DEFAULT(0),
        /**
         * 审批中
         */
        PROGRESSING(1),
        /**
         * 审批完成
         */
        COMPLETED(2),
        /**
         * 驳回
         */
        REJECTED(-1);

        Integer code;
    }
}
