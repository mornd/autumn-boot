package com.mornd.system.entity.po.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mornd.system.constant.EntityConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 10:20
 */
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
    private String modifiedBy;
    private Integer delFlag;
    private Long version;

    /**
     * 删除字段枚举
     */
    @AllArgsConstructor
    public enum Delete {
        /**
         * 删除字段枚举
         */
        DELETED(EntityConst.DELETED, "删除"), 
        NORMAL(EntityConst.NORMAL, "正常");
        @Getter
        private final Integer code;
        @Getter
        private final String name;
    }

    /**
     * 启用字段枚举
     */
    @AllArgsConstructor
    public enum EnableState {
        /**
         * 启用字段枚举
         */
        ENABLE(EntityConst.ENABLED, "启用"),
        DISABLE(EntityConst.DISABLED, "停用");
        @Getter
        private final Integer code;
        @Getter
        private final String name;
    }

    public enum BooleanLogic {
        /**
         * 布尔量
         */
        TRUE(1, "是"),
        FALSE(0, "否");

        @Getter
        private final Integer code;
        @Getter
        private final String name;

        BooleanLogic(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        private static Map<Integer, BooleanLogic> KEY_MAP = new HashMap<>();

        static {
            for (BooleanLogic value : BooleanLogic.values()) {
                KEY_MAP.put(value.getCode(), value);
            }
        }

        /**
         * 默认返回false
         *
         * @param code
         * @return
         */
        public static BooleanLogic getTypeDefaultFalse(Integer code) {
            BooleanLogic booleanLogic = KEY_MAP.get(code);
            if (null == booleanLogic) {
                return FALSE;
            }
            return booleanLogic;
        }

        /**
         * 获得布尔量
         *
         * @param code
         * @return
         */
        public static BooleanLogic getType(Integer code) {
            return KEY_MAP.get(code);
        }
    }
}
