package com.mornd.system.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 0:30
 * 第三方登录用户 dto
 */
@Data
public class OtherLoginUseDTO {
    @NotBlank(message = "uuid值不能为空")
    private String uuid;
    @NotBlank(message = "code值不能为空")
    private String code;
    @NotBlank(message = "source值不能为空")
    private String source;
}
