package com.mornd.system.entity.po;

import io.swagger.annotations.ApiModel;
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
    private String title;
    private String username;
    private String className;
    private String method;
    private Date visitDate;
    private Long executionTime;
    private String params;
    private String ip;
    private String url;
    private String osAndBrowser;
    private String result;
    private String exceptionMsg;
}
