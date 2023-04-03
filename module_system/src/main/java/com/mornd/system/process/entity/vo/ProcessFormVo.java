package com.mornd.system.process.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: mornd
 * @dateTime: 2023/4/3 - 22:35
 * 流程表单 vo 类
 */

@Data
public class ProcessFormVo {
    /**
     * 流程模板id
     */
    @NotNull(message = "流程模板id不能为空")
    private Long processTemplateId;

    /**
     * 流程类型id
     */
    @NotNull(message = "流程类型id不能为空")
    private Long processTypeId;

    /**
     * 表单 json 数据
     */
    private String formValues;
}
