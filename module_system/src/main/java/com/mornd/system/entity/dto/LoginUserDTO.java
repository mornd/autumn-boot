package com.mornd.system.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 12:33
 * 登录用户实体，封装用户登录时的字段
 */
@Data
@ApiModel("用户登录实体")
public class LoginUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录名称",required = true)
    @NotBlank(message = "登录名称不能为空")
    @Size(min = 3, max = 20, message = "账号长度须在3-20之间")
    private String username;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 3, max = 100, message = "加密后的密码长度须在3-60之间")
    private String password;

    @ApiModelProperty(value = "验证码",required = true)
    @NotBlank(message = "验证码不能为空")
    @Size(min = 1, max = 4, message = "验证码长度须在1-4之间")
    private String code;

    @ApiModelProperty(value = "验证uuid",required = true)
    //@NotBlank(message = "登录uuid不能为空")
    private String uuid;

    @ApiModelProperty("记住我功能")
    private Boolean remember;
}
