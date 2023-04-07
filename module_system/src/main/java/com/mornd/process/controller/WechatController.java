package com.mornd.process.controller;

import com.mornd.process.service.WechatService;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.controller.base.BaseController;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.process.entity.wechat.Menu;
import com.qiniu.util.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/4/6 - 20:31
 * 微信公众号 controller
 */

@RestController
@RequestMapping("/process/wechat")
@RequiredArgsConstructor
public class WechatController extends BaseController {

    private final WechatService wechatService;

    @GetMapping("/menu")
    @LogStar(title = "获取微信公众号菜单", businessType = LogType.SELECT)
    public JsonResult getMenu() {
        List<Menu> result = wechatService.getMenu();
        return JsonResult.successData(result);
    }

    @GetMapping("/menu/{id}")
    public JsonResult getMenuById(@PathVariable Long id) {
        Menu menu = wechatService.getMenuById(id);
        return JsonResult.successData(menu);
    }

    @PostMapping("/menu")
    @LogStar(title = "添加微信公众号菜单", businessType = LogType.INSERT)
    public JsonResult insertMenu(@RequestBody @Validated Menu menu) {
        wechatService.insertMenu(menu);
        return success();
    }

    @PutMapping("/menu")
    @LogStar(title = "修改微信公众号菜单", businessType = LogType.UPDATE)
    public JsonResult updateMenu(@RequestBody @Validated Menu menu) {
        wechatService.updateMenu(menu);
        return success();
    }

    @DeleteMapping("/menu/{id}")
    @LogStar(title = "添加微信公众号菜单", businessType = LogType.DELETE)
    public JsonResult deleteMenu(@PathVariable Long id) {
        wechatService.deleteMenu(id);
        return success();
    }
}
