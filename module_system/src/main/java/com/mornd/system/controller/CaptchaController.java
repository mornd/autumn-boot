package com.mornd.system.controller;

import com.mornd.system.constant.GlobalConst;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.utils.RedisUtil;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 11:33
 * 验证码加载
 */
@Slf4j
@RestController
@Api("获取验证码接口")
public class CaptchaController {
    @Resource
    private RedisUtil redisUtil;

    /**
     * 获取登录验证码
     * @param response
     * @param response
     * @return
     */
    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public JsonResult getCaptcha(HttpServletResponse response){
        //使用算数验证码
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(115, 40);
        //运算位数为2
        arithmeticCaptcha.setLen(2);
        //验证码结果
        String captchaResult = arithmeticCaptcha.text();
        String uuid = UUID.randomUUID().toString().replace("-","");
        log.info("登录验证码为：{}", captchaResult);
        log.info("登录uuid为：{}", uuid);

        Set<String> keys = redisUtil.keys(RedisKey.LOGIN_CAPTCHA_KEY + "*");
        redisUtil.delete(keys);
        //存入redis，并设置过期时间
        redisUtil.setValue(RedisKey.LOGIN_CAPTCHA_KEY + uuid,
                captchaResult,
                GlobalConst.CAPTCHA_EXPIRATION,
                TimeUnit.MINUTES);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("captcha", arithmeticCaptcha.toBase64());
        resultData.put("uuid", uuid);

        //设置响应头
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        return JsonResult.successData(resultData);
    }
}
