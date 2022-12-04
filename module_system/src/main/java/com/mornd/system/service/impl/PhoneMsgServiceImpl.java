package com.mornd.system.service.impl;

import cn.hutool.core.collection.IterUtil;
import com.mornd.system.config.async.factory.AsyncFactory;
import com.mornd.system.config.async.manager.AsyncManager;
import static com.mornd.system.constant.EntityConst.DISABLED;

import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.constant.EntityConst;
import static com.mornd.system.constant.RedisKey.PHONE_MSG_CODE;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysLoginInfor;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.service.*;
import com.mornd.system.utils.AliyunPhoneMsgUtil;
import com.mornd.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
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
    private PermissionService permissionService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private AliyunPhoneMsgUtil aliyunPhoneMsgUtil;

    /**
     * 发送登录短信
     * @param phone
     */
    @Override
    public void sendLoginPhoneMsgCode(final String phone) {
        final String code = generateRandomCode();
        boolean result = aliyunPhoneMsgUtil.clientSendMsg(phone, code);
        if(!result) throw new BadRequestException("短信发送失败，请重试");
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
            throw new BadRequestException("手机号码不存在");
        }
        if(DISABLED.equals(sysUser.getStatus())) {
            throw new BadRequestException("该账号已被禁用");
        }
        //设置角色、权限
        Set<SysRole> roles = roleService.findByUserId(sysUser.getId());
        if(IterUtil.isNotEmpty(roles)) {
            sysUser.setRoles(roles);
            List<String> ids = new ArrayList<>();
            roles.forEach(i -> ids.add(i.getId()));
            Set<SysPermission> pers = permissionService.getPersByRoleIds(ids, false, EntityConst.ENABLED);
            sysUser.setPermissions(pers);
        }

        AuthUser authUser = new AuthUser(sysUser);
        // 执行登录逻辑
        String token = authService.genericLogin(authUser);

        Map<String,Object> tokenMap = new HashMap<String, Object>(2) {{
            put("tokenHead", tokenProperties.getTokenHead());
            put("token", token);
        }};
        // 登录成功
        AsyncManager.me().execute(AsyncFactory.recordSysLoginInfor(sysUser.getId(), sysUser.getLoginName(), SysLoginInfor.Type.PHONE_MSG, SysLoginInfor.Status.SUCCESS, SysLoginInfor.Msg.SUCCESS.getMsg()));
        return JsonResult.success("登录成功", tokenMap);
    }

    /**
     * 生成四位的随机整数
     * @return
     */
    String generateRandomCode() {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        // 左闭右开取值
        final int code = threadLocalRandom.nextInt(1000, 10000);
        return String.valueOf(code);
    }
}
