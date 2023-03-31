package com.mornd.system.process.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mornd.system.validation.UpdateValidGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author: mornd
 * @dateTime: 2023/3/26 - 15:49
 */

@Data
@TableName("oa_process_template")
public class ProcessTemplate extends ProcessBaseEntity {

    @NotBlank(message = "名称不能为空")
    private String name;

    private String iconUrl;

    /**
     * 审批类型id
     */
    @NotNull(message = "审批类型不能为空")
    private Integer processTypeId;

    /**
     * 审批类型名称
     */
    @TableField(exist = false)
    private String processTypeName;

    private String formProps;

    private String formOptions;

    private String processDefinitionKey;

    private String processDefinitionFileName;

    private Integer processModelId;

    @Size(max = 500, message = "描述过长")
    private String description;

    private Integer status;

    @TableField(exist = false)
    private String stateStr;

    /**
     * 是否需要更新classes 下的流程文件
     */
    @TableField(exist = false)
    @NotNull(message = "updateFile属性不能为空", groups = UpdateValidGroup.class)
    private Boolean updateFile;

    @Getter
    @AllArgsConstructor
    public enum Status {
        /**
         * 未发布
         */
        UNPUBLISHED("未发布"),
        /**
         * 已发布
         */
        PUBLISHED("已发布");

        final String desc;
    }

}
