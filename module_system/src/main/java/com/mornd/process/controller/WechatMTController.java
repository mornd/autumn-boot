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


/**
 * @author: mornd
 * @dateTime: 2023/4/9 - 22:24
 * oa 移动端 controller
 */

@Controller
@RequestMapping("/process/wechatMT")
@RequiredArgsConstructor
public class WechatMTController extends BaseController {
    private final WechatService wechatService;

    /**
     * 微信授权
     * @param backUrl 前端回调地址
     * @return
     */
    @Anonymous
    @GetMapping("/authorize")
    public String authorize(@RequestParam("backUrl") String backUrl) {
        return wechatService.authorize(backUrl);
    }

    /**
     * 获取微信登录用户信息
     * @param code 固定值
     * @param state 固定值 ui界面回调地址
     * @return
     */
    @Anonymous
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String state) {
        return wechatService.userInfo(code, state);
    }

    /**
     * 微信登录绑定手机号
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
