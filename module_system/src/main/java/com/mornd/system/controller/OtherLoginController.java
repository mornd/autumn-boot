package com.mornd.system.controller;

import com.mornd.system.annotation.Anonymous;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.annotation.RateLimiter;
import com.mornd.system.annotation.RepeatSubmit;
import com.mornd.system.constant.RegexpConstant;
import com.mornd.system.constant.enums.LimitType;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.entity.dto.OtherLoginUseDTO;
import com.mornd.system.entity.dto.PhoneMsgLoginDTO;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.OtherLoginService;
import com.mornd.system.service.PhoneMsgService;
import com.mornd.system.utils.AliyunPhoneMsgUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Pattern;

/**
 * @author mornd
 * @dateTime 2022/10/19 - 22:11
 * 其它登录方式处理
 */
@Validated
@Anonymous
@RestController
public class OtherLoginController {
    @Resource
    private OtherLoginService otherLoginService;

    /**
     * gitee 方式登录
     * @return
     */
    @GetMapping("/preLoginByGitee")
    public JsonResult preLoginByGitee() {
        return otherLoginService.preLoginByGitee();
    }

    @LogStar(title = "gitee用户登录", BusinessType = LogType.LOGIN)
    @PostMapping("/loginByGitee")
    public JsonResult loginByGitee(@RequestBody @Validated OtherLoginUseDTO user) {
        return otherLoginService.loginByGitee(user);
    }

    @Resource
    PhoneMsgService phoneMsgService;

    /**
     * 发送手机验证码
     * @param phone 目标手机号码
     * @return
     */
    @RepeatSubmit(interval = 60000) //  一分钟内不允许相同的手机号提交
    // 一天只能发5次，阿里云默认也会限制
    @RateLimiter(count = 5, time = 86400, limitType = LimitType.IP, message = "system触发天级流控Permits:5")
    @GetMapping("/sendLoginPhoneMsgCode/{phone}")
    public JsonResult sendLoginPhoneMsgCode(@Pattern(regexp = RegexpConstant.PHONE, message = "手机号码格式不正确")
                                                @PathVariable(value = "phone")
                                                String phone) {
        phoneMsgService.sendLoginPhoneMsgCode(phone);
        return JsonResult.successData("发送成功，请注意查收短信，该验证码" + AliyunPhoneMsgUtil.CODE_TIME_OUT + "分钟内有效");
    }

    /**
     * 短信登录
     * @param loginDTO
     * @return
     */
    @LogStar(title = "短信登录", BusinessType = LogType.LOGIN)
    @RateLimiter(limitType = LimitType.IP)
    @PostMapping("/phoneMsgLogin")
    public JsonResult phoneMsgLogin(@Validated @RequestBody PhoneMsgLoginDTO loginDTO) {
        return phoneMsgService.phoneMsgLogin(loginDTO.getPhone(), loginDTO.getCode());
    }
}
