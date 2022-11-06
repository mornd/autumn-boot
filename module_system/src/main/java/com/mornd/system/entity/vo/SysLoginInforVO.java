package com.mornd.system.entity.vo;

import com.mornd.system.entity.po.SysLoginInfor;
import lombok.Data;

import java.util.Date;

/**
 * @author mornd
 * @dateTime 2022/11/6 - 19:26
 */
@Data
public class SysLoginInforVO extends SysLoginInfor {
    //根据访问时间搜索的日期范围
    private Date[] visitDateScope;
    private Integer pageNo;
    private Integer pageSize;
}
