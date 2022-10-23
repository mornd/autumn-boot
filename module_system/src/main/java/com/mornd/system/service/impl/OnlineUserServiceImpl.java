package com.mornd.system.service.impl;

import com.mornd.system.constant.RedisKey;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.OnlineUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.OnlineUserService;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.PageUtil;
import com.mornd.system.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mornd
 * @dateTime 2022/10/21 - 22:20
 */

@Service
public class OnlineUserServiceImpl implements OnlineUserService {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private AuthUtil authUtil;

    /**
     * 获取所有在线用户
     * @return
     */
    @Override
    public List<AuthUser> getAllAuthUser() {
        Set<String> keys = redisUtil.keys(RedisKey.ONLINE_USER_KEY + "*");
        List<AuthUser> userList = new ArrayList<>();
        for (String key : keys) {
            try {
                AuthUser authUser = (AuthUser) redisUtil.getValue(key);
                userList.add(authUser);
            } catch (Exception e) {
            }
        }
        return userList;
    }
    @Override
    public List<OnlineUser> getAllOnlineUser() {
        return getAllAuthUser().stream()
                .map(OnlineUser::new).collect(Collectors.toList());
    }

    @Override
    public JsonResult<?> pageList(OnlineUser user) {
        List<OnlineUser> userList = new ArrayList<>();
        // 总数
        long total = 0;
        for (OnlineUser onlineUser : getAllOnlineUser()) {
            String loginName = user.getLoginName();
            String realName = user.getRealName();

            String cacheLoginName = onlineUser.getLoginName();
            String cacheRealName = onlineUser.getRealName();

            // 条件筛选
            if(StringUtils.hasText(loginName) && StringUtils.hasText(realName)) {
                if(cacheLoginName.contains(loginName)
                        && cacheRealName.contains(realName)) {
                    userList.add(onlineUser);
                }
            } else if(StringUtils.hasText(loginName)) {
                if(cacheLoginName.contains(loginName)) {
                    userList.add(onlineUser);
                }
            } else if(StringUtils.hasText(realName)) {
                if(cacheRealName.contains(realName)) {
                    userList.add(onlineUser);
                }
            } else {
                userList.add(onlineUser);
            }
        }
        if(userList.size() > 0) {
            total = userList.size();
            // 按登录时间排序
            userList.sort((u1, u2) -> -u1.getLoginTime().compareTo(u2.getLoginTime()));
            userList = PageUtil.pageInfo(userList, user.getPageNo(), user.getPageSize());
        }
        Map<String,Object> map = new HashMap<>();
        map.put("data", userList);
        map.put("total", total);
        return JsonResult.successData(map);
    }

    /**
     * 强制退出
     * @param id
     */
    @Override
    public boolean kick(String id) {
        String key = getOnlineUserKeyById(id);
        return key != null && redisUtil.delete(key);
    }

    public String getOnlineUserKeyById(String id) {
        if(StringUtils.hasText(id)) {
            Set<String> keys = redisUtil.keys(RedisKey.ONLINE_USER_KEY + "*");
            for (String key : keys) {
                try {
                    AuthUser authUser = (AuthUser) redisUtil.getValue(key);
                    if(id.equals(authUser.getSysUser().getId())) {
                        return key;
                    }
                } catch (Exception e) {
                }
            }
        }
        return null;
    }
}
