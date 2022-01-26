package com.mornd.system.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mornd.system.validation.SelectValidGroup;
import com.mornd.system.validation.UpdateValidGroup;
import com.mornd.system.validation.ValidGroupA;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2022/1/19 - 8:26
 */
@Data
public class SysUserVO implements Serializable {
    @NotBlank(message = "ID不能为空", groups = {UpdateValidGroup.class, ValidGroupA.class})
    private String id;

    @NotBlank(message = "登录名不能为空")
    private String loginName;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotNull(message = "性别不能为空")
    @Range(min = 0, max = 1, message = "性别的值类型错误")
    private Integer gender;

    private Date birthday;

    private String phone;

    private Integer status;

    @NotBlank(message = "用户头像地址不能为空", groups = ValidGroupA.class)
    private String avatar;

    private String email;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date gmtCreate;
    
    Set<String> roles;
    
    @NotNull(message = "分页信息的起始页不能为空", groups = SelectValidGroup.class)
    private Integer pageNo;
    @NotNull(message = "分页信息的每页个数不能为空", groups = SelectValidGroup.class)
    private Integer pageSize;
}
