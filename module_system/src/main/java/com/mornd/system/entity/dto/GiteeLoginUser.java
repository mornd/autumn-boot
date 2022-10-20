package com.mornd.system.entity.dto;

import lombok.Data;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 0:30
 */
@Data
public class GiteeLoginUser {
    private String uuid;
    private String code;
    private String source;
}
