package com.mornd.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author mornd
 * @dateTime 2022/11/6 - 17:49
 * 登录记录
 */

@Data
@TableName("sys_logininfor")
@ApiModel("登录记录")
public class SysLoginInfor implements Serializable {
    private static final long serialVersionUID = -1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private Long  id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("状态(0：成功，1：失败) ")
    private Integer status;

    @ApiModelProperty("用户登录名")
    private String loginName;

    @ApiModelProperty("登录方式")
    private Integer type;

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("真实地址")
    private String address;

    @ApiModelProperty("浏览器")
    private String  browser;

    @ApiModelProperty("操作系统")
    private String  os;

    @ApiModelProperty("登录时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    @ApiModelProperty("消息")
    private String  msg;

    /**
     * 登录状态枚举
     */
    public enum Status {
        SUCCESS(0, "成功"),
        FAILURE(1, "失败");
        @Getter
        private final Integer code;
        @Getter
        private final String desc;

        private Status(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 登录提示消息枚举
     */
    public enum Msg {
        SUCCESS("登录成功"),
        USERNAME_NOT_FOUND("用户名或密码错误"),
        OTHER_ERROR("其它错误");
        @Getter
        private final String msg;

        private Msg(String  msg) {
            this.msg = msg;
        }
    }

    /**
     * 登录方式
     */
    public enum Type {
        ACCOUNT(0, "账号"),
        PHONE_MSG(1, "短信"),
        GITEE(2, " gitee"),
        WECHAT(3, "微信");
        @Getter
        private final Integer code;
        @Getter
        private final String desc;

        private Type(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
