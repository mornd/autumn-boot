package com.mornd.system.service;

import com.mornd.system.entity.dto.OtherLoginUseDTO;
import com.mornd.system.entity.result.JsonResult;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 0:35
 */
public interface OtherLoginService {
    JsonResult preLoginByGitee();

    JsonResult loginByGitee(OtherLoginUseDTO user);
}
