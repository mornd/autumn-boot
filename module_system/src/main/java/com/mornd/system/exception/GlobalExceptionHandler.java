package com.mornd.system.exception;

import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.constant.JsonResultCode;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/12 - 10:41
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public JsonResult exception(AccessDeniedException e){
        e.printStackTrace();
        log.info("权限不足");
        JsonResult<Object> failure = JsonResult.failure("权限不足");
        failure.setCode(403);
        return failure;
    }

    @ExceptionHandler(DataAccessException.class)
    public JsonResult exception(DataAccessException e){
        e.printStackTrace();
        log.info("数据访问异常：{}",e.getMessage());
        return JsonResult.failure("数据访问异常");
    }

    @ExceptionHandler(RedisCommandTimeoutException.class)
    public JsonResult exception(RedisCommandTimeoutException e){
        log.error("RedisCommandTimeoutException: redis服务连接异常");
        log.error(e.getMessage());
        return JsonResult.failure("redis服务异常，请稍后再重试");
    }

    @ExceptionHandler(QueryTimeoutException.class)
    public JsonResult exception(QueryTimeoutException e) {
        log.error("QueryTimeoutException: redis服务连接异常");
        log.error(e.getMessage());
        return JsonResult.failure("redis服务异常，请稍后再重试");
    }

    /**
     * 用户名或密码不存在
     * @param e
     * @return
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public JsonResult exception(UsernameNotFoundException e) {
        log.error(e.getMessage());
        return JsonResult.failure(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public JsonResult exception(RuntimeException e){
        e.printStackTrace();
        log.info("系统异常：{}",e.getMessage());
        return JsonResult.failure("系统运行时异常：" + e.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Throwable.class)
    public JsonResult exception(Throwable e){
        e.printStackTrace();
        log.info("系统异常2：{}",e.getMessage());
        return JsonResult.failure("系统异常2：" + e.toString());
    }

    /**
     * 实体Bean校验
     * @param exception
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getMessage();
        JsonResult jsonResult = new JsonResult();
        jsonResult.setSuccess(false);
        jsonResult.setCode(JsonResultCode.VIOLATION_EXCEPTION);
        jsonResult.setMessage(message);
        List<ObjectError> objectErrors = exception.getBindingResult().getAllErrors();
        if(!ObjectUtils.isEmpty(objectErrors)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < objectErrors.size(); i++) {
                sb.append(objectErrors.get(i).getDefaultMessage());
                if(i < (objectErrors.size() - 1)){
                    sb.append("，");
                }
            }
            jsonResult.setMessage(sb.toString());
        }
        log.info("参数校验错误：{}", jsonResult.getMessage());
        return jsonResult;
    }

    /**
     * 基于单个参数校验
     * @param exception
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public JsonResult handleConstraintViolationException(ConstraintViolationException exception){
        String message = exception.getMessage();
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        JsonResult jsonResult = new JsonResult();
        jsonResult.setSuccess(false);
        jsonResult.setCode(JsonResultCode.VIOLATION_EXCEPTION);
        jsonResult.setMessage(message);
        if(!ObjectUtils.isEmpty(constraintViolations)){
            StringBuilder sb = new StringBuilder();
            Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()){
                sb.append(iterator.next().getMessage());
                if(iterator.hasNext()){
                    sb.append("，");
                }
            }
            jsonResult.setMessage(sb.toString());
        }
        log.info("参数校验错误：{}", jsonResult.getMessage());
        return jsonResult;
    }
}
