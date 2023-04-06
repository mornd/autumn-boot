package com.mornd.system.process.service;

import com.mornd.system.process.entity.wechat.Menu;

import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/4/6 - 20:33
 */
public interface WechatService {
    List<Menu> getMenu(Menu menu);
}
