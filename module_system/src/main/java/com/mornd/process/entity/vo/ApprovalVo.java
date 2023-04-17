package com.mornd.process.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author: mornd
 * @dateTime: 2023/4/4 - 21:47
 * 审批 vo
 */

@Data
public class ApprovalVo {
    @NotNull(message = "流程id不能为空")
    private Long processId;

    @NotBlank(message = "流程任务id不能为空")
    private String taskId;

    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 拒绝原因
     */
    @Size(max = 500, message = "原因过长")
    private String reason;
}
