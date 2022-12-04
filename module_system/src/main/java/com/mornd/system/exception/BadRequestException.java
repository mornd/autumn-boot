package com.mornd.system.exception;

import org.springframework.http.HttpStatus;

import java.util.StringJoiner;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 20:44
 * 自定义异常处理
 */
public class BadRequestException extends RuntimeException {

    /**
     * 默认 status = 500
     */
    private Integer status = INTERNAL_SERVER_ERROR.value();

    public BadRequestException(String message){
        super(message);
    }

    public BadRequestException(HttpStatus status, String message){
        super(message);
        this.status = status.value();
    }

    public BadRequestException(Integer statusValue, String message) {
        super(message);
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
