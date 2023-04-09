package com.mornd.process.controller;

import com.mornd.process.service.WechatService;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.annotation.RateLimiter;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.controller.base.BaseController;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.process.entity.wechat.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/4/6 - 20:31
 * 微信公众号 controller
 */

@Validated
@RestController
@RequestMapping("/process/wechatMenu")
@RequiredArgsConstructor
public class WechatMenuController extends BaseController {
    private final WechatService wechatService;

    @GetMapping
    @LogStar(title = "获取流程公众号菜单", businessType = LogType.SELECT)
    public JsonResult getMenu() {
        List<Menu> result = wechatService.getMenu();
        return JsonResult.successData(result);
    }

    @GetMapping("/{id}")
    public JsonResult getMenuById(@PathVariable Long id) {
        Menu menu = wechatService.getMenuById(id);
        return JsonResult.successData(menu);
    }

    @PostMapping
    @LogStar(title = "添加流程公众号菜单", businessType = LogType.INSERT)
    public JsonResult insertMenu(@RequestBody @Validated Menu menu) {
        wechatService.insertMenu(menu);
        return success();
    }

    @PutMapping
    @LogStar(title = "修改流程公众号菜单", businessType = LogType.UPDATE)
    public JsonResult updateMenu(@RequestBody @Validated Menu menu) {
        wechatService.updateMenu(menu);
        return success();
    }

    @DeleteMapping("/{id}")
    @LogStar(title = "删除流程公众号菜单", businessType = LogType.DELETE)
    public JsonResult deleteMenu(@PathVariable Long id) {
        wechatService.deleteMenu(id);
        return success();
    }

    /**
     * 微信公众号平台测试账号申请：https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login
     * 微信公众号自定义菜单创建文档：https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Creating_Custom-Defined_Menu.html
     * 内网穿透工具：https://ngrok.cc/user.html
     * @return
     */
    @RateLimiter(time = 86400, count = 10)
    @PostMapping("/sync")
    @LogStar(title = "同步微信公众号菜单", businessType = LogType.SYNC)
    public JsonResult syncMenu() {
        wechatService.syncMenu();
        return JsonResult.success("同步成功，公众号显示可能会有延迟，需稍等几秒钟。");
    }

    @DeleteMapping("/deleteAll")
    @LogStar(title = "删除微信公众号所有的菜单", businessType = LogType.DELETE)
    public JsonResult deleteAllMenu() {
        wechatService.deleteAllMenu();
        return JsonResult.success("公众号菜单删除成功");
    }
}
