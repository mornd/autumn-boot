package com.mornd.system.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author mornd
 * @dateTime 2021/12/25 - 21:59
 * 修改密码传输对象
 */

@Data
public class ChangePwdDTO implements Serializable {

    @Size(min = 3, max = 20, message = "原密码长度须在3-20之间")
    private String oldPwd;
    @Size(min = 3, max = 20, message = "新密码须在3-20之间")
    private String newPwd;
}
