package com.mornd.system.service;

import com.mornd.system.entity.dto.GiteeLoginUser;
import com.mornd.system.entity.result.JsonResult;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 0:35
 */
public interface OtherLoginService {
    JsonResult loginByGitee(GiteeLoginUser user);
}
