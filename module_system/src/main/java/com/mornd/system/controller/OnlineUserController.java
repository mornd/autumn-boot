package com.mornd.system.controller;

import com.mornd.system.annotation.Anonymous;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.OnlineUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.utils.PageUtil;
import com.mornd.system.utils.RedisUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 19:27
 */

@RestController
@RequestMapping("/onlineUser")
public class OnlineUserController {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private TokenProperties tokenProperties;

    @PreAuthorize("hasAuthority('onlineUser')")
    @LogStar("获取在线用户列表")
    @GetMapping
    public JsonResult<?> list(@Validated OnlineUser user) {
        Set<String> keys = redisUtil.keys(tokenProperties.getOnlineUserKey() + "*");
        List<OnlineUser> userList = new ArrayList<>();
        long total = 0;
        for (String key : keys) {
            try {
                AuthUser authUser = (AuthUser) redisUtil.getValue(key);
                String loginName = user.getLoginName();
                String realName = user.getRealName();

                String cacheLoginName = authUser.getSysUser().getLoginName();
                String cacheRealName = authUser.getSysUser().getRealName();
                if(StringUtils.hasText(loginName) && StringUtils.hasText(realName)) {
                    if(cacheLoginName.contains(loginName)
                            && cacheRealName.contains(realName)) {
                        userList.add(new OnlineUser(authUser));
                    }
                } else if(StringUtils.hasText(loginName)) {
                    if(cacheLoginName.contains(loginName)) {
                        userList.add(new OnlineUser(authUser));
                    }
                } else if(StringUtils.hasText(realName)) {
                    if(cacheRealName.contains(realName)) {
                        userList.add(new OnlineUser(authUser));
                    }
                } else {
                    userList.add(new OnlineUser(authUser));
                }
            } catch (Exception e) {
            }
        }
        if(userList.size() > 0) {
            // 按登录时间排序
            total = userList.size();
            userList.sort(Comparator.comparing(OnlineUser::getLoginTime));
            userList = PageUtil.pageInfo(userList, user.getPageNo(), user.getPageSize());
        }
        Map<String,Object> map = new HashMap<>();
        map.put("data", userList);
        map.put("total", total);
        return JsonResult.successData(map);
    }

    /**
     * 强制踢人
     * @param loginName
     * @return
     */
    @PreAuthorize("hasAuthority('onlineUser:kick')")
    @LogStar("强制踢人")
    @DeleteMapping("/{loginName}")
    public JsonResult kick(@PathVariable String loginName) {
        String key = tokenProperties.getOnlineUserKey() + loginName + "*";
        long result = redisUtil.deleteKeysPattern(key);
        return JsonResult.success("操作成功");
    }
}
