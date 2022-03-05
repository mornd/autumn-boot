package com.mornd.system.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author mornd
 * @dateTime 2021/12/28 - 14:57
 */

@Data
@ApiModel("系统操作日志")
public class SysLog implements Serializable {
    @ApiModelProperty("主题")
    private String title;
    @ApiModelProperty("操作用户")
    private String username;
    @ApiModelProperty("方法名")
    private String methodName;
    
    @ApiModelProperty("访问时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date visitDate;
    @ApiModelProperty("操作时长")
    private Long executionTime;
    @ApiModelProperty("日志类型")
    private Integer type;
    @ApiModelProperty("参数")
    private String params;
    @ApiModelProperty("访问ip")
    private String ip;
    @ApiModelProperty("访问url")
    private String url;
    @ApiModelProperty("操作系统浏览器")
    private String osAndBrowser;
    
    @ApiModelProperty("异常信息")
    private String exceptionMsg;
    
    @ApiModelProperty("访问结果")
    private String result;
}
