package com.mornd.system.exception;

import com.mornd.system.constant.JsonResultCode;

import java.io.Serializable;

/**
 * @author: mornd
 * @dateTime: 2022/12/11 - 22:51
 * @description:
 */
public class AutumnException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 14543434L;

    /**
     * 错误码,默认 status = 500
     */
    private Integer code = JsonResultCode.COMMON_EXCEPTION;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     *
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public AutumnException()
    {
    }

    public AutumnException(String message)
    {
        super();
        this.message = message;
    }

    public AutumnException(Integer code, String message)
    {
        this(message);
        this.code = code;
    }

    public AutumnException(Integer code, String message, String detailMessage)
    {
        this(message);
        this.code = code;
        this.detailMessage = detailMessage;
    }

    public Integer getCode()
    {
        return code;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public String getDetailMessage()
    {
        return detailMessage;
    }

    public AutumnException setCode(Integer code) {
        this.code = code;
        return this;
    }

    public AutumnException setMessage(String message)
    {
        this.message = message;
        return this;
    }

    public AutumnException setDetailMessage(String detailMessage)
    {
        this.detailMessage = detailMessage;
        return this;
    }
}
