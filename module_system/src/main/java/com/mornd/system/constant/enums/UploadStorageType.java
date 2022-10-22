package com.mornd.system.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mornd
 * @dateTime 2022/10/22 - 20:17
 * 文件存储位置
 */
public enum UploadStorageType {
    LOCAL("local","本地上传"),
    QINIU("qiniu","七牛云");

    @Getter
    private final String type;
    @Getter
    private final String desc;

    private UploadStorageType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
