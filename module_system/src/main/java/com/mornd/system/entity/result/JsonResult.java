package com.mornd.system.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        return success("成功");
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
        return failure("失败");
    }

    public static JsonResult<Object> failure(String message){
        return failure(message, null);
    }

    public static JsonResult<Object> failure(String message, Object data){
        return failure(JsonResultCode.COMMON_EXCEPTION, message, data);
    }

    public static JsonResult<Object> failure(Integer code, String message, Object data){
        JsonResult<Object> jsonResult = new JsonResult<>();
        jsonResult.setSuccess(false);
        jsonResult.setCode(code);
        jsonResult.setMessage(message);
        jsonResult.setData(data);
        return jsonResult;
    }
}
