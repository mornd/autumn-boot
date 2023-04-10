package com.mornd.system.controller;

import com.mornd.system.annotation.Anonymous;
import com.mornd.system.annotation.RateLimiter;
import com.mornd.system.annotation.RepeatSubmit;
import com.mornd.system.constant.GlobalConst;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.constant.RegexpConstant;
import com.mornd.system.constant.enums.LimitType;
import com.mornd.system.entity.dto.ForgetPwdFormDTO;
import com.mornd.system.entity.dto.LoginUserDTO;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.AuthService;
import com.mornd.system.service.PhoneMsgService;
import com.mornd.system.utils.AliyunPhoneMsgUtil;
import com.mornd.system.utils.AutumnUUID;
import com.mornd.system.utils.RedisUtil;
import com.mornd.system.utils.SecretUtil;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 12:42
 * 提供用户登录，退出的接口
 */

@Slf4j
@Validated
@Api("用户认证接口")
@RestController
@RequestMapping
public class AuthController {
    @Resource
    private AuthService authService;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 用户登录
     * @param loginUserDTO
     * @return
     */
    @Anonymous
    @RateLimiter(time = 3600, count = 10, limitType = LimitType.IP, message = "登录太过于频繁，请稍后重试")
    @ApiOperation("用户登录")
    @PostMapping("/userLogin")
    public JsonResult userLogin(@RequestBody @Validated LoginUserDTO loginUserDTO){
        return authService.userLogin(loginUserDTO);
    }
    @Anonymous
    @ApiOperation("用户注销")
    @PostMapping("/userLogout")
    public JsonResult userLogout(HttpServletRequest request){
        return authService.userLogout(request);
    }

    /**
     * 获取登录验证码
     * @param response
     * @param response
     * @return
     */
    @Anonymous
    @RateLimiter(time = 10, count = 5, limitType = LimitType.IP)
    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public JsonResult getCaptcha(HttpServletResponse response){
        //使用算数验证码
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(115, 40);
        //运算位数为2
        arithmeticCaptcha.setLen(2);
        //验证码结果
        String captchaResult = arithmeticCaptcha.text();
        String uuid = AutumnUUID.fastSimpleUUID();
        log.info("登录验证码为：{}", captchaResult);
        log.info("登录uuid为：{}", uuid);

        //存入redis，并设置过期时间
        redisUtil.setValue(RedisKey.LOGIN_CAPTCHA_KEY + uuid,
                captchaResult,
                GlobalConst.CAPTCHA_EXPIRATION,
                TimeUnit.MINUTES);

        Map<String, Object> resultData = new HashMap<String, Object>(2) {{
            put("captcha", arithmeticCaptcha.toBase64());
            put("uuid", uuid);
        }};
        //设置响应头
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        return JsonResult.successData(resultData);
    }

    @Resource
    private PhoneMsgService phoneMsgService;

    @Anonymous
    @RepeatSubmit(interval = 60000)
    // 一天只能发5次，阿里云默认也会限制
    @RateLimiter(count = 5, time = 86400, limitType = LimitType.IP, message = "system触发天级流控Permits:5")
    @ApiOperation("忘记密码-发送短信验证码")
    @GetMapping("/sendForgetPwdPhoneMsgCode/{phone}")
    public JsonResult sendForgetPwdPhoneMsgCode(@Pattern(regexp = RegexpConstant.PHONE, message = "手机号码格式不正确")
                                                    @PathVariable(value = "phone")
                                                    String phone) {
        phoneMsgService.sendForgetPwdPhoneMsgCode(phone);
        return JsonResult.successData("发送成功，请注意查收短信，该验证码" + AliyunPhoneMsgUtil.CODE_TIME_OUT + "分钟内有效");
    }

    @Anonymous
    @RateLimiter(limitType = LimitType.IP)
    @ApiOperation("忘记密码-修改新密码")
    @PostMapping("/forgetPwdUpdatePwd")
    public JsonResult checkForgetPwdCode(@Validated @RequestBody ForgetPwdFormDTO formDTO) {
        // 解密密码
        final String desPwd = SecretUtil.desEncrypt(formDTO.getNewPwd());
        boolean result = phoneMsgService.updatePwd(formDTO.getPhone(), formDTO.getCode(), desPwd);
        return JsonResult.successData(result);
    }
}
