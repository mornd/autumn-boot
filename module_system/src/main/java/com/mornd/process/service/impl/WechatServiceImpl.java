package com.mornd.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.process.entity.wechat.Menu;
import com.mornd.process.service.WechatService;
import com.mornd.process.mapper.wechat.MenuMapper;
import com.mornd.system.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
