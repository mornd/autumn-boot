package com.mornd.system.entity.dto;

import com.mornd.system.constant.RegexpConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author: mornd
 * @dateTime: 2022/12/4 - 0:36
 * @description:
 */
@Data
public class PhoneMsgLoginDTO {
    @Pattern(regexp = RegexpConstant.PHONE, message = "手机号码格式不正确")
    private String phone;
    @NotBlank(message = "验证码不能为空")
    private String code;
}
