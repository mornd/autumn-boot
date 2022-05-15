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

    @NotBlank(message = "登录名不能为空")
    @Size(min = 1, max = 20, message = "账号长度须在1-20之间")
    @ApiModelProperty(value = "登录名",required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 3, max = 100, message = "加密后的密码长度须在3-60之间")
    @ApiModelProperty(value = "密码",required = true)
    private String password;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 1, max = 4, message = "验证码长度须在1-4之间")
    @ApiModelProperty(value = "验证码",required = true)
    private String code;

    @NotBlank(message = "登录uuid不能为空")
    @ApiModelProperty(value = "验证uuid",required = true)
    private String uuid;
    
    @ApiModelProperty(value = "密码是否需要先解密")
    private Boolean desEncrypt;

    @ApiModelProperty("记住我功能")
    private Boolean remember;
}
