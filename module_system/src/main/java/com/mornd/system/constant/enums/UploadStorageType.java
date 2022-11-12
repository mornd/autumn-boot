package com.mornd.system.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mornd
 * @dateTime 2022/10/22 - 20:17
 * 文件存储位置
 */
public enum UploadStorageType {
    LOCAL(1, "本地上传"),
    QINIU(2, "七牛云"),
    ALIYUN(3, "阿里云oss");

    @Getter
    private final Integer code;
    @Getter
    private final String desc;

    private UploadStorageType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
