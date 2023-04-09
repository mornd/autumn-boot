package com.mornd.process.service.impl;

import cn.hutool.core.collection.IterUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.process.config.WechatAccountConfig;
import com.mornd.process.entity.vo.BindPhoneVo;
import com.mornd.process.entity.wechat.Menu;
import com.mornd.process.service.WechatService;
import com.mornd.process.mapper.wechat.MenuMapper;
import com.mornd.system.config.AutumnConfig;
import com.mornd.system.config.async.factory.AsyncFactory;
import com.mornd.system.config.async.manager.AsyncManager;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysLoginInfor;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.AutumnException;
import com.mornd.system.service.AuthService;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserService userService;
    private final AuthService authService;


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
                        // oa 移动端页面地址
                        view.put("url", autumnConfig.getOaUiBaseUrl() + "/#" + child.getUrl());
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
        log.info("开始微信授权，回调地址为：{}", backUrl);
        String redirectUrl = wxMpService.getOAuth2Service()
                        .buildAuthorizationUrl(accountConfig.getUserInfoUrl(),
                                WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                                URLEncoder.encode(backUrl.replace("autumnoa", "#")));
        log.info("【微信网页授权】获取code，redirectUrl：{}", redirectUrl);
        return "redirect:" + redirectUrl;
    }

    /**
     * 微信用户登录并返回token
     * @param code
     * @param backUrl
     * @return
     */
    @Override
    public String userInfo(String code, String backUrl) {
        log.info("微信获取用户信息，code：{}，backUrl：{}", code, backUrl);
        try {
            WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
            String openId = accessToken.getOpenId();
            System.out.println("openId--->" + openId);

            // 获取微信用户信息
            WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
            System.out.println("wxmpuserInfo---->" + JSON.toJSONString(userInfo));

            // 根据 openId 查询数据库
            LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
            qw.eq(SysUser::getOpenId, openId);
            qw.select(SysUser::getOpenId);
            SysUser sysUser = userService.getOne(qw);
            if(sysUser != null) {
                // 执行登录逻辑
                String token = authService.genericLogin(new AuthUser(sysUser));
                // 记录登录日志
                AsyncManager.me().execute(AsyncFactory.recordSysLoginInfor(sysUser.getId(), sysUser.getLoginName(), SysLoginInfor.Type.WECHAT, SysLoginInfor.Status.SUCCESS, SysLoginInfor.Msg.SUCCESS.getMsg()));
                String symbol = backUrl.contains("?") ? "&" : "?";
                return String.format("redirect:%s%stoken=%s&openId=%s", backUrl, symbol, token, openId);
            }
        } catch (WxErrorException e) {
            log.info("获取微信用户信息失败：{}", e.getMessage());
        }
        throw new AutumnException("获取微信用户信息失败");
    }

    /**
     * 绑定手机号
     * @param vo
     * @return
     */
    @Override
    public String bindPhone(BindPhoneVo vo) {
        log.info("微信绑定手机号，参数为：{}", vo.toString());
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
        qw.eq(SysUser::getPhone, vo.getPhone());
        SysUser sysUser = userService.getOne(qw);
        if(sysUser != null) {
            LambdaUpdateWrapper<SysUser> uw = Wrappers.lambdaUpdate(SysUser.class);
            uw.set(SysUser::getOpenId, vo.getOpenId());
            uw.eq(SysUser::getId, sysUser.getId());
            userService.update(uw);

            // 执行登录逻辑
            String token = authService.genericLogin(new AuthUser(sysUser));
            // 记录登录日志
            AsyncManager.me().execute(AsyncFactory.recordSysLoginInfor(sysUser.getId(), sysUser.getLoginName(), SysLoginInfor.Type.WECHAT, SysLoginInfor.Status.SUCCESS, SysLoginInfor.Msg.SUCCESS.getMsg()));
            return token;
        } else {
            throw new AutumnException("手机号绑定失败");
        }
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
