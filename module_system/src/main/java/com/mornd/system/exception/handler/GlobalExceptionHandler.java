package com.mornd.system.exception.handler;

import com.mornd.system.constant.ResultMessage;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.constant.JsonResultCode;
import com.mornd.system.exception.AutumnException;
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
import org.springframework.validation.BindException;
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
        String msg = String.format("请求地址'%s',不支持'%s'请求", requestURI, e.getMethod());
        log.error(msg);
        return JsonResult.failure(msg);
    }

    @ExceptionHandler(DataAccessException.class)
    public JsonResult handleException(DataAccessException e, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生数据访问异常.", requestURI, e);
        return JsonResult.failure("数据访问异常");
    }

    @ExceptionHandler(RedisCommandTimeoutException.class)
    public JsonResult handleException(RedisCommandTimeoutException e){
        log.error("RedisCommandTimeoutException: redis服务连接异常");
        log.error(e.getMessage());
        return JsonResult.failure("redis服务异常，请稍后再重试");
    }

    @ExceptionHandler(QueryTimeoutException.class)
    public JsonResult handleException(QueryTimeoutException e) {
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
    public JsonResult handleException(UsernameNotFoundException e) {
        log.error(e.getMessage());
        return JsonResult.failure(e.getMessage());
    }

    /**
     * 用户的用户名不存在或密码不正确抛出 BadCredentialsException 异常
     * 准确来说是密码不匹配才会抛出该异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public JsonResult handleBadCredentialsException(BadCredentialsException e){
        log.error(e.getMessage(), e);
//        return JsonResult.failure(ResultMessage.PASSWORD_ERROR);
        return JsonResult.failure(e.getMessage());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = AutumnException.class)
    public JsonResult handleAutumnException(AutumnException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生自定义AutumnException异常.", requestURI, e);
        return JsonResult.failure(e.getCode(), e.getMessage());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = BadRequestException.class)
    public JsonResult handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生自定义BadRequestException异常.", requestURI, e);
        return JsonResult.failure(e.getStatus(), e.getMessage());
    }

    /**
     * 处理未知异常
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public JsonResult handleException(Throwable e, HttpServletRequest request){
        e.printStackTrace();
        log.error(e.getMessage());
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        //return JsonResult.failure("未知错误，请联系管理员");
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
     * 数据校验异常
     * @param exception
     * @return
     */
    @ExceptionHandler(BindException.class)
    public JsonResult handleConstraintViolationException(BindException exception){
        JsonResult jsonResult = new JsonResult();
        jsonResult.setSuccess(false);
        jsonResult.setCode(JsonResultCode.VIOLATION_EXCEPTION);
        List<ObjectError> allErrors = exception.getAllErrors();
        StringBuilder sb = new StringBuilder();
        Iterator<ObjectError> iterator = allErrors.iterator();
        while (iterator.hasNext()){
            sb.append(iterator.next().getDefaultMessage());
            if(iterator.hasNext()){
                sb.append("，");
            }
        }
        jsonResult.setMessage(sb.toString());
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
