package com.mornd.system.exception;

import org.springframework.http.HttpStatus;

import java.util.StringJoiner;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 20:44
 * 自定义异常处理
 */
public class BadRequestException extends RuntimeException{

    private Integer status = BAD_REQUEST.value();

    public BadRequestException(String msg){
        super(msg);
    }

    public BadRequestException(HttpStatus status, String msg){
        super(msg);
        this.status = status.value();
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
