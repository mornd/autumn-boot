package com.mornd.system.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.system.process.entity.wechat.Menu;
import com.mornd.system.process.mapper.wechat.MenuMapper;
import com.mornd.system.process.service.WechatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/4/6 - 20:35
 */

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class WechatServiceImpl implements WechatService {

    private final MenuMapper menuMapper;


    /**
     * 查询微信公众号的所有菜单
     * @return
     */
    @Override
    public List<Menu> getMenu(Menu menu) {
        LambdaQueryWrapper<Menu> qw = Wrappers.lambdaQuery(Menu.class);
        qw.orderByAsc(Menu::getSort);
        List<Menu> menuList = menuMapper.selectList(qw);
        return buildTree(menuList);
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
