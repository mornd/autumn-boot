package com.mornd.process.service.impl;

import cn.hutool.core.collection.IterUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.process.config.WechatAccountConfig;
import com.mornd.process.entity.wechat.Menu;
import com.mornd.process.service.WechatService;
import com.mornd.process.mapper.wechat.MenuMapper;
import com.mornd.system.config.AutumnConfig;
import com.mornd.system.exception.AutumnException;
import com.mornd.system.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/4/6 - 20:35
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class WechatServiceImpl implements WechatService {

    private final MenuMapper menuMapper;
    private final WxMpService wxMpService;
    private final AutumnConfig autumnConfig;
    private final WechatAccountConfig accountConfig;


    /**
     * 查询微信公众号的所有菜单
     * @return
     */
    @Override
    public List<Menu> getMenu() {
        LambdaQueryWrapper<Menu> qw = Wrappers.lambdaQuery(Menu.class);
        qw.orderByAsc(Menu::getSort);
        List<Menu> menuList = menuMapper.selectList(qw);
        return buildTree(menuList);
    }

    @Override
    public Menu getMenuById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public void insertMenu(Menu menu) {
        if(menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        menu.setCreateId(SecurityUtil.getLoginUserId());
        menu.setCreateTime(LocalDateTime.now());
        menuMapper.insert(menu);
    }

    @Override
    public void updateMenu(Menu menu) {
        menu.setUpdateId(SecurityUtil.getLoginUserId());
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        // 先删除改菜单的子集再删除本身
        this.recursiveDelete(id);
    }

    /**
     * 递归删除菜单
     * @param id
     */
    private void recursiveDelete(Long id) {
        Menu menu = menuMapper.selectById(id);
        if(menu != null) {
            LambdaQueryWrapper<Menu> qw = Wrappers.lambdaQuery(Menu.class);
            qw.eq(Menu::getParentId, id);
            List<Menu> menus = menuMapper.selectList(qw);
            if(!menus.isEmpty()) {
                for (Menu children : menus) {
                    this.recursiveDelete(children.getId());
                }
            }
            menuMapper.deleteById(id);
        }
    }

    @Override
    public void syncMenu() {
        // 将菜单转为 wx 要求的格式
        List<Menu> menus = this.getMenu();

        JSONArray buttonList = new JSONArray();

        for (Menu menu : menus) {
            JSONObject one = new JSONObject();
            one.put("name", menu.getName());
            if(IterUtil.isEmpty(menu.getChildren())) {
                one.put("type", menu.getType());
                one.put("url", autumnConfig.getUiBaseUrl() + menu.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for (Menu child : menu.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("name", child.getName());
                    view.put("type", child.getType());

                    if("view".equals(child.getType())) {
                        // h5 页面地址
                        view.put("url", "http://oa_atguigu.cn/#" + child.getUrl());
                    } else {
                        view.put("key", child.getMenuKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }

        JSONObject button = new JSONObject();
        button.put("button", buttonList);

        // 调用工具方法推送菜单
        try {
            String result = wxMpService.getMenuService().menuCreate(button.toString());
            log.info("同步公众号菜单结果：{}", result);
        } catch (WxErrorException e) {
            throw new AutumnException("同步公众号菜单发生异常，" + e.getMessage());
        }
    }

    @Override
    public void deleteAllMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new AutumnException("删除公众号菜单发生异常，" + e.getMessage());
        }
    }

    /**
     *
     * @param backUrl 授权成功跳转的路径
     * @return
     */
    @Override
    public String authorize(String backUrl) {
        String redirectUrl = wxMpService.getOAuth2Service()
                        .buildAuthorizationUrl(accountConfig.getUserInfoUrl(),
                                WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                                URLEncoder.encode(backUrl.replace("autumn", "#")));
        log.info("【微信网页授权】获取code，redirectUrl：{}", redirectUrl);
        return "redirect:" + redirectUrl;
    }

    /**
     * 构建菜单树形结构
     * @param menuList
     * @return
     */
    private List<Menu> buildTree(List<Menu> menuList) {
        // 根菜单的 parentId
        final Long rootParentId = 0L;
        List<Menu> tree = new ArrayList<>();
        for (Menu menu : menuList) {
            if(rootParentId.equals(menu.getParentId())) {
                tree.add(getChildren(menu, menuList));
            }
        }
        return tree;
    }

    /**
     * 查询某个菜单的子集
     * @param menu
     * @param menuList
     * @return
     */
    private Menu getChildren(Menu menu, List<Menu> menuList) {
        for (Menu item : menuList) {
            if(menu.getId().equals(item.getParentId())) {
                if(menu.getChildren() == null) {
                    menu.setChildren(new ArrayList<>());
                }
                menu.getChildren().add(getChildren(item, menuList));
            }
        }
        return menu;
    }
}
