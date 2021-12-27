package com.mornd.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:55
 */
public interface SysUserService extends IService<SysUser> {
    SysUser findByUsername(String username);

    boolean verifyCurrentPassword(String oldPwd);

    JsonResult changePwd(String oldPwd, String newPwd);
}
