package com.mornd.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysUserVO;

import java.util.List;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:55
 */
public interface UserService extends IService<SysUser> {
    boolean verifyCurrentPassword(String oldPwd);

    JsonResult changePwd(String oldPwd, String newPwd);

    JsonResult pageList(SysUserVO user);

    List<SysUserVO> export(SysUserVO userVO);

    JsonResult insert(SysUserVO user);

    JsonResult update(SysUserVO user);

    JsonResult delete(String id);

    JsonResult changeStatus(String id, Integer state);

    boolean queryLoginNameExists(String name, String id);

    JsonResult getRoleById(String id);

    int updateAvatar(SysUser user);

    JsonResult userUpdate(SysUserVO user);

    /**
     * 根据手机号码查询用户
     * @param phone
     * @return
     */
    SysUser getUserByPhone(String phone);

    boolean queryPhoneExists(String phone, String id);
}
