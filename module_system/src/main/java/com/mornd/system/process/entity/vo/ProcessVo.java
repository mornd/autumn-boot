package com.mornd.system.process.entity.vo;

import com.mornd.system.process.entity.Process;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTimeBegin;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTimeEnd;


    private String userName;

    private String processTypeName;

    private String processTemplateName;

    private String currentAuditorName;

}
