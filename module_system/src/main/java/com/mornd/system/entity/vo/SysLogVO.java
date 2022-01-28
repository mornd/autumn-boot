package com.mornd.system.entity.vo;

import com.mornd.system.entity.po.SysLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mornd
 * @dateTime 2022/1/28 - 16:09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogVO extends SysLog {
    private Integer pageNo;
    private Integer pageSize;
}
