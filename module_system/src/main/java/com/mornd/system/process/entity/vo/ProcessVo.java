package com.mornd.system.process.entity.vo;

import com.mornd.system.process.entity.Process;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2023/3/30 - 22:18
 * 返回结果 vo 类
 */
@Data
public class ProcessVo extends Process {
    /**
     * 搜索关键字
     */
    private String keyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeBegin;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

    private String userName;
    private String userRealName;

    private String processTypeName;

    private String processTemplateName;

    /**
     * 当前审批人信息
     */
    private String currentAuditorRealName;
    private String currentAuditorInfo;

}
