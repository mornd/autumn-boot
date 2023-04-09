package com.mornd.process.service;

import com.mornd.process.entity.wechat.Menu;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/4/6 - 20:33
 */
public interface WechatService {
    List<Menu> getMenu();

    void insertMenu(Menu menu);

    void updateMenu(Menu menu);

    void deleteMenu(Long id);

    Menu getMenuById(Long id);

    void syncMenu();

    void deleteAllMenu();

    String authorize(String backUrl);
}