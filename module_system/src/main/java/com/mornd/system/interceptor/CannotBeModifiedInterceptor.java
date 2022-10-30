package com.mornd.system.interceptor;

import com.mornd.system.annotation.Anonymous;
import com.mornd.system.constant.enums.LoginUserSource;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.utils.RespUtil;
import com.mornd.system.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author mornd
 * @dateTime 2022/10/25 - 22:23
 */
@Slf4j
@Component
public class CannotBeModifiedInterceptor implements HandlerInterceptor {
    /**
     * 非系统不能修改或删除数据
     * @param request 请求
     * @param response 响应
     * @param handler 目标方法
     * @return 是否进入系统 controller
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 目标方法对象
            Method method = handlerMethod.getMethod();
            if(handlerMethod.hasMethodAnnotation(Anonymous.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(Anonymous.class)) {
                return true;
            }
            SysUser loginUser;
            try {
                loginUser = SecurityUtil.getLoginUser();
            } catch (Exception e) {
                return true;
            }
            if(LoginUserSource.LOCAL.getCode().equals(loginUser.getSource())) {
                return true;
            }
            // get 方式不会影响数据，放行
            if(HttpMethod.GET.matches(request.getMethod())) {
                return true;
            }
            RespUtil.writeResult(response, JsonResult.failure("非系统用户不允许修改或删除数据！"));
            return false;
        } else {
            return true;
        }
    }
}
