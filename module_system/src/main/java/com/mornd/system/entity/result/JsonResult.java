package com.mornd.system.entity.result;

import com.mornd.system.constant.JsonResultCode;
import com.mornd.system.constant.ResultMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 11:45
 * 接口返回数据格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Boolean success;
    private Integer code;
    private String message;
    private T data;
    private Long timestamp = System.currentTimeMillis();

    public static JsonResult<Object> success(){
        return success(defaultSuccessMessage());
    }

    public static JsonResult<Object> success(String message){
        return success(message, null);
    }

    public static JsonResult<Object> success(String message, Object data){
        JsonResult<Object> jsonResult = new JsonResult<>();
        jsonResult.setSuccess(true);
        jsonResult.setCode(JsonResultCode.SUCCESS_CODE);
        jsonResult.setMessage(message);
        jsonResult.setData(data);
        return jsonResult;
    }

    /**
     * 操作成功，并返回数据
     * @param data
     * @return
     */
    public static JsonResult<Object> successData(Object data){
        return success(null, data);
    }

    /**
     * 返回空数据
     * @return
     */
    public static JsonResult<Object> successEmpty() {
        return success(null, null);
    }


    public static JsonResult<Object> failure(){
        return failure(defaultFailureMessage());
    }

    public static JsonResult<Object> failure(String message){
        return failure(message, null);
    }

    public static JsonResult<Object> failure(String message, Object data){
        return failure(JsonResultCode.COMMON_EXCEPTION, message, data);
    }

    public static JsonResult<Object> failure(Integer code, String message){
        return failure(code, message, null);
    }
    
    public static JsonResult<Object> failure(Integer code, String message, Object data){
        JsonResult<Object> jsonResult = new JsonResult<>();
        jsonResult.setSuccess(false);
        jsonResult.setCode(code);
        jsonResult.setMessage(message);
        jsonResult.setData(data);
        return jsonResult;
    }

    private static String defaultSuccessMessage() {
        String message = "成功";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String method = request.getMethod();
        switch (method.toUpperCase()) {
            case "GET":
                message = "获取数据成功";
                break;
            case "POST":
                message = ResultMessage.INSERT_MSG;
                break;
            case "PUT":
                message = ResultMessage.UPDATE_MSG;
                break;
            case "DELETE":
                message = ResultMessage.DELETE_MSG;
                break;
            default:
                break;
        }
        return message;
    }

    private static String defaultFailureMessage() {
        String message = "失败";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String method = request.getMethod();
        switch (method.toUpperCase()) {
            case "GET":
                message = "获取数据失败，请重试";
                break;
            case "POST":
                message = ResultMessage.INSERT_FAILURE_MSG;
                break;
            case "PUT":
                message = ResultMessage.UPDATE_FAILURE_MSG;
                break;
            case "DELETE":
                message = ResultMessage.DELETE_FAILURE_MSG;
                break;
            default:
                break;
        }
        return message;
    }
}
