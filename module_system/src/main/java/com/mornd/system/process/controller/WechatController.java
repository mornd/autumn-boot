package com.mornd.system.process.controller;

import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.process.entity.wechat.Menu;
import com.mornd.system.process.service.WechatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/4/6 - 20:31
 * 微信公众号 controller
 */

@RestController
@RequestMapping("/process/wechat")
@RequiredArgsConstructor
public class WechatController {

    private final WechatService wechatService;

    @GetMapping("/menu")
    @LogStar(title = "获取微信公众号菜单", businessType = LogType.SELECT)
    public JsonResult getMenu(Menu menu) {
        List<Menu> result = wechatService.getMenu(menu);
        return JsonResult.successData(result);
    }


}
