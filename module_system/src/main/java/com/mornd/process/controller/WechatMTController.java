package com.mornd.process.controller;

import com.mornd.process.entity.vo.BindPhoneVo;
import com.mornd.process.service.WechatService;
import com.mornd.system.annotation.Anonymous;
import com.mornd.system.controller.base.BaseController;
import com.mornd.system.entity.result.JsonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * @author: mornd
 * @dateTime: 2023/4/9 - 22:24
 * oa 移动端 controller
 */

@Controller
@Validated
@RequestMapping("/process/wechatMT")
@RequiredArgsConstructor
public class WechatMTController extends BaseController {
    private final WechatService wechatService;

    @Anonymous
    @GetMapping("/authorize")
    public String authorize(@RequestParam("backUrl")
                                @NotBlank(message = "url不能为空")
                                String backUrl) {
        return wechatService.authorize(backUrl);
    }

    /**
     * 获取微信登录用户信息
     * @param code
     * @param backUrl
     * @return
     */
    @Anonymous
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("backUrl") String backUrl) {
        return wechatService.userInfo(code, backUrl);
    }

    /**
     * 微信账号绑定手机号
     * @return
     */
    @Anonymous
    @ResponseBody
    @PostMapping("/bindPhone")
    public JsonResult bindPhone(@Validated @RequestBody BindPhoneVo vo) {
        String token = wechatService.bindPhone(vo);
        return JsonResult.successData(token);
    }
}
