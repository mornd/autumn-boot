package com.mornd.system.service;

import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.OnlineUser;
import com.mornd.system.entity.result.JsonResult;

import java.util.List;

/**
 * @author mornd
 * @dateTime 2022/10/21 - 22:19
 */
public interface OnlineUserService {
    List<AuthUser> getAllAuthUser();

    List<OnlineUser> getAllOnlineUser();

    JsonResult<?> pageList(OnlineUser user);

    boolean kick(String id);

    String getOnlineUserKeyById(String id);
}
