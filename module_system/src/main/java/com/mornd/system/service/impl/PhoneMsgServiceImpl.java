package com.mornd.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.system.config.async.factory.AsyncFactory;
import com.mornd.system.config.async.manager.AsyncManager;
import static com.mornd.system.constant.EntityConst.DISABLED;

import com.mornd.system.config.security.components.TokenProperties;

import static com.mornd.system.constant.RedisKey.FORGET_PWD_PHONE_MSG_CODE;
import static com.mornd.system.constant.RedisKey.PHONE_MSG_CODE;

import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysLoginInfor;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.service.*;
import com.mornd.system.utils.AliyunPhoneMsgUtil;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author: mornd
 * @dateTime: 2022/12/4 - 0:14
 * @description: 短信登录业务处理层
 */

@Slf4j
@Service
public class PhoneMsgServiceImpl implements PhoneMsgService {
    @Resource
    private UserService userService;

    @Resource
    private AuthService authService;

    @Resource
    private RoleService roleService;

    @Resource
    private TokenProperties tokenProperties;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private AliyunPhoneMsgUtil aliyunPhoneMsgUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private AuthUtil authUtil;

    /**
     * 发送登录短信
     * @param phone
     */
    @Override
    public void sendLoginPhoneMsgCode(final String phone) {
//        SysUser sysUser = userService.getUserByPhone(phone);
//        if(sysUser == null) {
//            throw new BadRequestException("改手机号码不属于本系统");
//        }
        final String code = generateRandomCode();
        aliyunPhoneMsgUtil.clientSendMsg(phone, code);
        redisUtil.setValue(PHONE_MSG_CODE + phone, code, AliyunPhoneMsgUtil.CODE_TIME_OUT, TimeUnit.MINUTES);
    }

    /**
     * 短信验证登录
     * @param phone 手机号码
     * @param code 验证码
     * @return
     */
    @Override
    public JsonResult phoneMsgLogin(final String phone, final String code) {
        // 从缓存中获取验证码
        String value = (String) redisUtil.getValue(PHONE_MSG_CODE + phone);
        if(value == null || !value.equalsIgnoreCase(code)) {
            throw new BadRequestException("验证码错误");
        }
        redisUtil.delete(PHONE_MSG_CODE + phone);
        SysUser sysUser = userService.getUserByPhone(phone);
        if(sysUser == null) {
            throw new BadRequestException("改手机号码不属于本系统");
        }
        if(DISABLED.equals(sysUser.getStatus())) {
            throw new BadRequestException("该账号已被禁用");
        }
        roleService.setLoginUserPermissions(sysUser);

        AuthUser authUser = new AuthUser(sysUser);
        // 执行登录逻辑
        String token = authService.genericLogin(authUser);

        Map<String,Object> tokenMap = new HashMap<String, Object>(2) {{
            put("tokenHead", tokenProperties.getTokenHead());
            put("token", token);
        }};
        // 记录登录日志
        AsyncManager.me().execute(AsyncFactory.recordSysLoginInfor(sysUser.getId(), sysUser.getLoginName(), SysLoginInfor.Type.PHONE_MSG, SysLoginInfor.Status.SUCCESS, SysLoginInfor.Msg.SUCCESS.getMsg()));
        return JsonResult.success("登录成功", tokenMap);
    }

    /**
     * 发送忘记密码功能的短信验证码
     * @param phone 要发送的手机号码
     */
    @Override
    public void sendForgetPwdPhoneMsgCode(final String phone) {
        final String code = generateRandomCode();
        aliyunPhoneMsgUtil.clientSendMsg(phone, code);
        redisUtil.setValue(FORGET_PWD_PHONE_MSG_CODE + phone, code, AliyunPhoneMsgUtil.CODE_TIME_OUT, TimeUnit.MINUTES);
    }

    /**
     * 忘记密码-重新设置新密码
     * @param phone 手机号码
     * @param code 验证码
     * @param newPwd 新密码
     * @return
     */
    @Override
    public boolean updatePwd(String phone, String code, String newPwd) {
        String value = (String) redisUtil.getValue(FORGET_PWD_PHONE_MSG_CODE + phone);
        if(value == null || !value.equalsIgnoreCase(code)) {
            throw new BadRequestException("验证码错误");
        }
        // 删除验证码
        redisUtil.delete(FORGET_PWD_PHONE_MSG_CODE + phone);
        //加密新密码
        String encode = passwordEncoder.encode(newPwd);
        // 更新数据库
        LambdaUpdateWrapper<SysUser> qw = Wrappers.lambdaUpdate(SysUser.class);
        qw.eq(SysUser::getPhone, phone);
        qw.set(SysUser::getPassword, encode);
        userService.update(null, qw);
        // 清除缓存
        authUtil.delCacheLoginUser();
        return true;
    }

    /**
     * 生成六位的随机整数
     * @return
     */
    String generateRandomCode() {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        // 左闭右开取值
        final int code = threadLocalRandom.nextInt(100000, 1000000);
        return String.valueOf(code);
    }
}
