package com.mornd.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.vo.SysUserVO;

import java.util.List;
import java.util.Set;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:55
 */
public interface UserService extends IService<SysUser> {
    boolean verifyCurrentPassword(String oldPwd);

    void changePwd(String oldPwd, String newPwd);

    IPage<SysUserVO> pageList(SysUserVO user);

    List<SysUserVO> export(SysUserVO userVO);

    void insert(SysUserVO user);

    void update(SysUserVO user);

    void delete(String id);

    void changeStatus(String id, Integer state);

    boolean queryLoginNameExists(String name, String id);

    Set<String> getRoleById(String id);

    int updateAvatar(SysUser user);

    void userUpdate(SysUserVO user);

    /**
     * 根据手机号码查询用户
     * @param phone
     * @return
     */
    SysUser getUserByPhone(String phone);

    /**
     * 根据id查询手机号是否存在
     * @param phone
     * @param id
     * @return
     */
    boolean queryPhoneExists(String phone, String id);
}
