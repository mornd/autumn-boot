package com.mornd.system.controller;

import com.mornd.system.annotation.Anonymous;
import com.mornd.system.annotation.RateLimiter;
import com.mornd.system.annotation.RepeatSubmit;
import com.mornd.system.constant.enums.LimitType;
import com.mornd.system.entity.dto.OtherLoginUseDTO;
import com.mornd.system.entity.dto.PhoneMsgLoginDTO;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.OtherLoginService;
import com.mornd.system.service.PhoneMsgService;
import com.mornd.system.utils.AliyunPhoneMsgUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author mornd
 * @dateTime 2022/10/19 - 22:11
 * 其它登录方式处理
 */
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
    @RepeatSubmit(interval = 2000) // 2秒内不允许相同的手机号提交
    @RateLimiter(count = 1, limitType = LimitType.IP) // 60秒之后才能继续发送短信，我这里是心疼money，否则去掉count属性就行
    @GetMapping("/sendLoginPhoneMsgCode/{phone}")
    public JsonResult sendLoginPhoneMsgCode(@PathVariable(value = "phone") String phone) {
        phoneMsgService.sendLoginPhoneMsgCode(phone);
        return JsonResult.successData("发送成功，请注意查收短信，该验证码" + AliyunPhoneMsgUtil.CODE_TIME_OUT + "分钟内有效");
    }

    /**
     * 短信登录
     * @param loginDTO
     * @return
     */
    @RateLimiter(limitType = LimitType.IP)
    @PostMapping("/phoneMsgLogin")
    public JsonResult phoneMsgLogin(@Validated @RequestBody PhoneMsgLoginDTO loginDTO) {
        return phoneMsgService.phoneMsgLogin(loginDTO.getPhone(), loginDTO.getCode());
    }
}
