package com.mornd.process.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/3/25 - 22:16
 * 审批类型
 */

@Data
@TableName("oa_process_type")
public class ProcessType extends ProcessBaseEntity {
    /**
     * 类型名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 描述 (小于等于500可以通过校验，大于500则报错)
     */
    @Size(max = 500, message = "描述过长")
    private String description;

    /**
     * 审批模板
     */
    @TableField(exist = false)
    private List<ProcessTemplate> processTemplateList;
}
