package com.mornd.system.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mornd.system.config.security.components.JwtTokenUtil;
import com.mornd.system.constant.RedisKey;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.entity.dto.LoginUserDTO;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.LoginService;
import com.mornd.system.utils.RedisUtil;
import com.mornd.system.utils.SecretUtil;
import com.mornd.system.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/9/6 - 8:56
 */
@Slf4j
@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public JsonResult userLogin(LoginUserDTO loginUserDTO) {
        log.info("用户{}正在执行登录操作", loginUserDTO.getUsername());
        //验证码校验
        String uuid = loginUserDTO.getUuid();
        String captcha = (String) redisUtil.getValue(RedisKey.LOGIN_CAPTCHA_KEY + uuid);
        //模糊匹配删除key
        Set<String> keys = redisUtil.keys(RedisKey.LOGIN_CAPTCHA_KEY + "*");
        redisUtil.delete(keys);
        if(StringUtils.isBlank(captcha)){
            return JsonResult.failure("验证码不存在或已过期");
        }
        if(!captcha.equalsIgnoreCase(loginUserDTO.getCode().trim())){
            return JsonResult.failure("验证码错误");
        }
        if(loginUserDTO.getDesEncrypt() != null && loginUserDTO.getDesEncrypt()) {
            //密码解密
            String inputPwd = SecretUtil.desEncrypt(loginUserDTO.getPassword());
            loginUserDTO.setPassword(inputPwd);
        }
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginUserDTO.getUsername());
        if(userDetails == null || !passwordEncoder.matches(loginUserDTO.getPassword(), userDetails.getPassword())){
            log.info("用户{}，登录失败：{}", loginUserDTO.getUsername(), ResultMessage.USER_NOTFOUND);
            return JsonResult.failure(ResultMessage.USER_NOTFOUND);
        }
        if(!userDetails.isEnabled()){
            return JsonResult.failure("该账号已被禁用");
        }
        //本项目未使用以下验证，所以会一直为true
        if(!userDetails.isAccountNonLocked()){
            return JsonResult.failure("该账号已被锁定");
        }
        if(!userDetails.isAccountNonExpired()){
           return JsonResult.failure("该账号已过期");
        }
        if(!userDetails.isCredentialsNonExpired()){
            return JsonResult.failure("该账号凭证已过期");
        }

        //将登录用户信息存入security上下文对象中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //生成token
        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String,String> tokenMap = new HashMap<>(2);

        tokenMap.put("tokenHead", tokenHead);
        tokenMap.put("token", token);

        log.info("用户{}登录系统成功", userDetails.getUsername());
        return JsonResult.success("登录成功", tokenMap);
    }

    /**
     * 用户退出
     * @param request
     * @param response
     * @return
     */
    @Override
    public JsonResult userLogout(HttpServletRequest request, HttpServletResponse response) {
        String username = SecurityUtil.getLoginUsername();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        /*if(authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }*/

        if(authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
            //SecurityContextHolder.clearContext();
        }

        redisUtil.delete(RedisKey.CURRENT_USER_INFO_KEY + username);
        log.info("用户{}执行注销操作成功！",username);
        return JsonResult.success("注销成功");
    }
}
