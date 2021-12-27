package com.mornd.system.entity.po.base;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
    private Date modifiedBy;
    private Integer delFlag;
    private Long version;

    /**
     * 删除字段枚举
     */
    public enum Delete {
        /**
         * 删除字段枚举
         */
        DELETED(0, "删除"), NORMAL(1, "正常");
        @Getter
        private final Integer code;
        @Getter
        private final String name;

        Delete(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        private static Map<Integer, Delete> KEY_MAP = new HashMap<>();

        static {
            for (Delete value : Delete.values()) {
                KEY_MAP.put(value.getCode(), value);
            }
        }

        public static Delete getType(Integer code) {
            return KEY_MAP.get(code);
        }
    }

    /**
     * 启用字段枚举
     */
    public enum EnableState {
        /**
         * 启用字段枚举
         */
        ENABLE(1, "启用"),
        DISABLE(0, "停用");
        @Getter
        private final Integer code;
        @Getter
        private final String name;

        EnableState(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        private static Map<Integer, EnableState> KEY_MAP = new HashMap<>();

        static {
            for (EnableState value : EnableState.values()) {
                KEY_MAP.put(value.getCode(), value);
            }
        }

        public static EnableState getType(Integer code) {
            return KEY_MAP.get(code);
        }
    }

    public enum BooleanLogic {
        // 布尔量
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
