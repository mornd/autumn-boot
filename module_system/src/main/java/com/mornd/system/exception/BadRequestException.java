package com.mornd.system.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.StringJoiner;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 20:44
 * 自定义异常处理
 */
public class BadRequestException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 645437658L;

    /**
     * 默认 status = 500
     */
    private Integer status = INTERNAL_SERVER_ERROR.value();

    public BadRequestException() {}

    public BadRequestException(String message){
        super(message);
    }

    public BadRequestException(HttpStatus status, String message){
        this(status.value(), message);
    }

    public BadRequestException(Integer statusValue, String message) {
        this(message);
        this.status = statusValue;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BadRequestException.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("message=" + super.getMessage())
                .toString();
    }
}
