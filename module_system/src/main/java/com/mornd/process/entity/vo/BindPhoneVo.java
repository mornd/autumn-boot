package com.mornd.process.entity.vo;

import com.mornd.system.constant.RegexpConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author: mornd
 * @dateTime: 2023/4/9 - 23:44
 */

@Data
public class BindPhoneVo {

    @Pattern(regexp = RegexpConstant.PHONE, message = "手机号码格式不正确")
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "openId不能为空")
    private String openId;
}
