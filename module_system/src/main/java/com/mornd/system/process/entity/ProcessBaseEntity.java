package com.mornd.system.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mornd.system.validation.UpdateValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2023/3/26 - 15:47
 */
@Getter
@Setter
public class ProcessBaseEntity implements Serializable {
    private static final long serialVersionUID = -7710L;

    /**
     * 主键
     */
    @NotNull(message = "ID不能为空", groups = {UpdateValidGroup.class})
    @TableId(type = IdType.AUTO)
    private Long id;

    private String createId;

    // 将返回参数序列化
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss")
    // 将请求参数序列化
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String  updateId;
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Integer pageNo;
    @TableField(exist = false)
    private Integer pageSize;

    public Integer getPageNo() {
        return (pageNo == null || pageNo < 1) ? 1 : pageNo;
    }

    public Integer getPageSize() {
        return (pageSize == null || pageSize < 1) ? 10 : pageSize;
    }
}
