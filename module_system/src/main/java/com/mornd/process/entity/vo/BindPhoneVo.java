package com.mornd.process.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: mornd
 * @dateTime: 2023/4/9 - 23:44
 */

@Data
public class BindPhoneVo {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "openId不能为空")
    private String openId;
}
