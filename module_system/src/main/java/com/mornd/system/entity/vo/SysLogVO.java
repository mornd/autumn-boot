package com.mornd.system.entity.vo;

import com.mornd.system.entity.po.SysLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author mornd
 * @dateTime 2022/1/28 - 16:09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogVO extends SysLog {
    //根据访问时间搜索的日期范围
    private Date[] visitDateScope;
    private Integer pageNo;
    private Integer pageSize;
}
