package com.mornd.system.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mornd.system.constant.RegexpConstant;
import com.mornd.system.utils.EasyExcelConverter;
import com.mornd.system.validation.SelectValidGroup;
import com.mornd.system.validation.UpdateValidGroup;
import com.mornd.system.validation.ValidGroupA;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2022/1/19 - 8:26
 */
@Data
public class SysUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    @NotBlank(message = "ID不能为空", groups = {UpdateValidGroup.class, ValidGroupA.class})
    private String id;

    @ExcelProperty(value = "登录名")
    @NotBlank(message = "登录名不能为空")
    @Pattern(regexp = RegexpConstant.ACCOUNT, message = RegexpConstant.ACCOUNT_MESSAGE)
    private String loginName;

    @ExcelProperty(value = "真实名称")
    @NotBlank(message = "姓名不能为空")
    private String realName;

    @ExcelProperty(value = "性别", converter = EasyExcelConverter.GenderConverter.class)
    @NotNull(message = "性别不能为空")
    @Range(min = 0, max = 1, message = "性别的值类型错误")
    private Integer gender;

    @ExcelProperty(value = "出生日期")
    @DateTimeFormat("yyyy-MM-dd")
    private Date birthday;

    @ExcelProperty(value = "手机号码")
    private String phone;

    @ExcelProperty(value = "状态", converter = EasyExcelConverter.StatusConverter.class)
    private Integer status;

    @ExcelProperty(value = "头像")
    @NotBlank(message = "用户头像地址不能为空", groups = ValidGroupA.class)
    private String avatar;

    private String email;

    @ExcelProperty(value = "创建日期")
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date gmtCreate;

    @ExcelIgnore
    Set<String> roles;

    @ExcelIgnore
    @NotNull(message = "分页信息的起始页不能为空", groups = SelectValidGroup.class)
    private Integer pageNo;

    @ExcelIgnore
    @NotNull(message = "分页信息的每页个数不能为空", groups = SelectValidGroup.class)
    private Integer pageSize;
}
