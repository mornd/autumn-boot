package com.mornd.system.service.impl;

import com.mornd.system.config.async.factory.AsyncFactory;
import com.mornd.system.config.async.manager.AsyncManager;
import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.config.security.components.TokenProvider;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.dto.LoginUserDTO;
import com.mornd.system.entity.po.SysLoginInfor;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.AuthService;
import com.mornd.system.service.OnlineUserService;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.RedisUtil;
import com.mornd.system.utils.SecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:26
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    @Resource
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Resource
    private TokenProvider tokenProvider;
    @Resource
    private TokenProperties tokenProperties;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private AuthUtil authUtil;
    @Resource
    private OnlineUserService onlineUserService;

    /**
     * 处理用户登录逻辑
     * @param loginUserDTO
     * @return
     */
    @Override
    public JsonResult<?> userLogin(LoginUserDTO loginUserDTO) {
        //验证码校验
        String uuid = loginUserDTO.getUuid();
        String captcha = (String) redisUtil.getValue(RedisKey.LOGIN_CAPTCHA_KEY + uuid);
        redisUtil.delete(RedisKey.LOGIN_CAPTCHA_KEY + uuid);
        if(!StringUtils.hasText(captcha)){
            return JsonResult.failure("验证码不存在或已过期");
        }
        if(!captcha.equalsIgnoreCase(loginUserDTO.getCode().trim())){
            return JsonResult.failure("验证码错误");
        }

        //将密码重新赋新值，避免日志切面读取密码解密后的值
        String inputPwd;
        if(loginUserDTO.getDesEncrypt() != null && loginUserDTO.getDesEncrypt()) {
            //密码解密
            inputPwd = SecretUtil.desEncrypt(loginUserDTO.getPassword());
        } else {
            inputPwd = loginUserDTO.getPassword();
        }

        // 认证对象
        Authentication authenticate = null;
        try {
            // 执行登录逻辑
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), inputPwd);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            /**
             * 源码：
             *  authenticationManagerBuilder.getObject() 就是 ProviderManager 对象
             *  ProviderManager.authenticate() 182行=> result = provider.authenticate(authentication);
             *  AbstractUserDetailsAuthenticationProvider.authenticate() 133行=> user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
             *          上面的 AbstractUserDetailsAuthenticationProvider 是抽象类，会执行其子类 DaoAuthenticationProvider 的 retrieveUser方法
             *          AbstractUserDetailsAuthenticationProvider 147行 -> additionalAuthenticationChecks()校验密码
             *  DaoAuthenticationProvider的retrieveUser() 93行=> UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);
             */
            authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (Exception e) {
            // 登录失败，记录失败日志
            AsyncManager.me().execute(AsyncFactory.recordSysLoginInfor(null, loginUserDTO.getUsername(), SysLoginInfor.Type.ACCOUNT, SysLoginInfor.Status.FAILURE, e.getMessage()));
            throw e;
        }
        AuthUser principal = (AuthUser) authenticate.getPrincipal();
        // 执行登录，并生成token
        String token = this.genericLogin(principal);
        // 返回结果
        Map<String,Object> tokenMap = new HashMap<String, Object>(2) {{
            put("tokenHead", tokenProperties.getTokenHead());
            put("token", token);
        }};
        // 登录成功
        AsyncManager.me().execute(AsyncFactory.recordSysLoginInfor(principal.getSysUser().getId(), loginUserDTO.getUsername(), SysLoginInfor.Type.ACCOUNT, SysLoginInfor.Status.SUCCESS, SysLoginInfor.Msg.SUCCESS.getMsg()));
        return JsonResult.success("登录成功", tokenMap);
    }

    /**
     * 通用登录方法，返回 token
     * @param authUser security 用户
     * @return
     */
    @Override
    public String genericLogin(AuthUser authUser) {
        if(tokenProperties.getSingleLogin()) {
            // 单用户登录，移除其它登录过的用户 key
            onlineUserService.kick(authUser.getSysUser().getId());
        }

        //  生成登录用户的 IP，操作系统等
        authUtil.generateLoginInfo(authUser);

        // 生成 token
        String token = tokenProvider.generateToken(authUser);

        // 将登录用户信息存入 redis 中
        redisUtil.setValue(authUtil.getLoginUserRedisKey(token),
                authUser,
                tokenProperties.getExpiration(),
                tokenProperties.getExpirationTimeUnit());
        return token;
    }

    /**
     * 处理用户退出逻辑
     * @param request
     * @return
     */
    @Override
    public JsonResult userLogout(HttpServletRequest request) {
        String token = tokenProvider.searchToken(request);
        String subject = tokenProvider.getClaims(token).getSubject();
        redisUtil.delete(authUtil.getLoginUserRedisKey(token));
        log.info("用户{}注销成功", subject);
        return JsonResult.success("退出成功");
    }
}
