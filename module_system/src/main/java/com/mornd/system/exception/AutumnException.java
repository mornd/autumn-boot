package com.mornd.system.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author: mornd
 * @dateTime: 2022/12/11 - 22:51
 * @description:
 */
public class AutumnException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码,默认 status = 500
     */
    private Integer code = INTERNAL_SERVER_ERROR.value();

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
        this.message = message;
    }

    public AutumnException(HttpStatus status, String message){
        this.code = status.value();
        this.message = message;
    }

    public AutumnException(Integer code, String message)
    {
        this.code = code;
        this.message = message;
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
