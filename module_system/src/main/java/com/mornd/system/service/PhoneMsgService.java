package com.mornd.system.service;

import com.mornd.system.entity.result.JsonResult;

/**
 * @author: mornd
 * @dateTime: 2022/12/4 - 0:14
 * @description:
 */
public interface PhoneMsgService {

    JsonResult phoneMsgLogin(String phone, String code);

    void sendLoginPhoneMsgCode(String phone);

    void sendForgetPwdPhoneMsgCode(String phone);

    boolean updatePwd(String phone, String code, String newPwd);
}
