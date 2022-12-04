package com.mornd.system.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author: mornd
 * @dateTime: 2022/12/4 - 22:36
 * @description: 忘记密码提交表单
 */

@Data
public class ForgetPwdFormDTO extends PhoneMsgLoginDTO {
    @NotBlank(message = "新密码不能为空")
    @Size(min = 3, max = 100, message = "加密后的密码长度须在3-60之间")
    private String newPwd;
}
