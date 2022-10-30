package com.mornd.system.exception.handler;

import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.constant.JsonResultCode;
import com.mornd.system.exception.BadRequestException;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/12 - 10:41
 * 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 权限校验异常 403，处理 controller 层添加的权限注解
     */
    @ExceptionHandler(AccessDeniedException.class)
    public JsonResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return JsonResult.failure(HttpStatus.FORBIDDEN.value(), "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public JsonResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                          HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return JsonResult.failure(e.getMessage());
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

    /**
     * 用户用户名不存在或密码不正确抛出 BadCredentialsException 异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public JsonResult badCredentialsException(BadCredentialsException e){
        log.error(e.getMessage());
        return JsonResult.failure(e.getMessage());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = BadRequestException.class)
    public JsonResult badRequestException(BadRequestException e) {
        // 打印堆栈信息
        log.error(e.getMessage());
        return JsonResult.failure(e.getMessage());
    }

    /**
     * 处理所有的未知异常
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public JsonResult exception(Throwable e){
        e.printStackTrace();
        log.error(e.getMessage());
        return JsonResult.failure(e.getMessage());
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
