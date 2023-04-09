package com.mornd.system.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.system.config.async.factory.AsyncFactory;
import com.mornd.system.config.async.manager.AsyncManager;
import com.mornd.system.constant.EntityConst;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.constant.enums.EnumHiddenType;
import com.mornd.system.constant.enums.LoginUserSource;
import com.mornd.system.entity.dto.AuthUser;

import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.entity.dto.OtherLoginUseDTO;
import com.mornd.system.entity.po.SysLoginInfor;
import com.mornd.system.entity.po.SysPermission;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.po.temp.RoleWithPermission;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.mapper.RoleWithPermissionMapper;
import com.mornd.system.service.AuthService;
import com.mornd.system.service.OtherLoginService;
import com.mornd.system.service.PermissionService;
import com.mornd.system.service.RoleService;
import com.mornd.system.utils.AutumnUUID;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;

import me.zhyd.oauth.request.AuthGiteeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 0:35
 */
@Slf4j
@Service
public class OtherLoginServiceImpl implements OtherLoginService {
    @Resource
    private TokenProperties tokenProperties;
    @Resource
    private AuthService authService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;
    @Resource
    private RoleWithPermissionMapper roleWithPermissionMapper;

    // 登录gitee，点击设置-第三方应用中申请
    @Value("${otherLogin.gitee.clientId}")
    private String clientId;
    @Value("${otherLogin.gitee.secret}")
    private String secret;
    @Value("${otherLogin.gitee.uri}")
    private String uri;

    @Override
    public JsonResult preLoginByGitee() {
        AuthGiteeRequest authRequest =
                new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(clientId).clientSecret(secret).redirectUri(uri).build());
        String uuid = AutumnUUID.fastUUID();
        String authorizeUrl = authRequest.authorize(uuid);
        Map<String,Object> map = new HashMap<>();
        map.put("authorizeUrl", authorizeUrl);
        map.put("uuid", uuid);
        return JsonResult.successData(map);
    }

    /**
     * 目前 gitee 用户没有真正的进入系统，只赋予了 游客 的身份登录
     * @param user
     * @return
     */
    @Override
    public JsonResult loginByGitee(OtherLoginUseDTO user) {
        final String uuid = user.getUuid();
        final String code = user.getCode();
        final String source =user.getSource();

        AuthGiteeRequest authGiteeRequest =
                new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(clientId).clientSecret(secret).redirectUri(uri).build());
        // 执行登录
        AuthResponse<me.zhyd.oauth.model.AuthUser> loginUser =
                authGiteeRequest.login(AuthCallback.builder().state(uuid).code(code).build());
        me.zhyd.oauth.model.AuthUser userData = loginUser.getData();
        userData.getSource();
        userData.getToken();
        userData.getLocation();
        userData.getRawUserInfo();
        // todo gitee 用户进入系统

        AuthUser authUser = setGiteeSysUser(userData);

        // Security 上下文手动设置登录用户，不然拦截器会有问题
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(authUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 执行登录逻辑，返回 token
        String token = authService.genericLogin(authUser);

        // 记录登录日志
        AsyncManager.me().execute(AsyncFactory.recordSysLoginInfor(null, authUser.getUsername(), SysLoginInfor.Type.ACCOUNT, SysLoginInfor.Status.SUCCESS, SysLoginInfor.Msg.SUCCESS.getMsg()));

        Map<String,Object> tokenMap = new HashMap<String, Object>(3) {{
            put("tokenHead", tokenProperties.getTokenHead());
            put("token", token);
            put("msg", "目前gitee用户只能访问页面，不能增删改数据！");
        }};
        return JsonResult.success("登录成功", tokenMap);
    }

    /**
     * 封装成系统用户
     * @param userData gitee 返回的用户信息
     * @return 系统用户
     */
    private AuthUser setGiteeSysUser(me.zhyd.oauth.model.AuthUser userData) {
        AuthUser authUser = new AuthUser();
        SysUser sysUser = new SysUser();
        sysUser.setId(userData.getUuid());
        sysUser.setLoginName(formatUserName(userData.getUsername()));
        sysUser.setRealName(userData.getNickname());
        sysUser.setAvatar(userData.getAvatar());
        sysUser.setEmail(userData.getEmail());
        sysUser.setSource(LoginUserSource.GITEE.getCode());

        // 设置gitee用户的权限
        LambdaQueryWrapper<RoleWithPermission> qw = Wrappers.lambdaQuery();

        // 获取 gitee 用户专属角色
        LambdaQueryWrapper<SysRole> qw2 = Wrappers.lambdaQuery(SysRole.class);
        qw2.select(SysRole::getId);
        qw2.eq(SysRole::getCode, SecurityConst.GITEE_ROLE);
        SysRole role = roleService.getOne(qw2);

        qw.eq(RoleWithPermission::getRoleId, role.getId());
        List<RoleWithPermission> rolePerms = roleWithPermissionMapper.selectList(qw);
        LambdaQueryWrapper<SysPermission> qw3 = Wrappers.lambdaQuery();
        qw3.eq(SysPermission::getHidden, EnumHiddenType.DISPLAY.getCode());
        qw3.eq(SysPermission::getEnabled, EntityConst.ENABLED);
        qw3.in(SysPermission::getId, rolePerms.stream().map(RoleWithPermission::getPerId).collect(Collectors.toList()));
        List<SysPermission> perms = permissionService.list(qw3);

        sysUser.setPermissions(perms.stream().map(SysPermission::getCode).collect(Collectors.toSet()));
        authUser.setSysUser(sysUser);
        return authUser;
    }

    private String formatUserName(String username) {
        return String.format("%s(%s)", username, LoginUserSource.GITEE.getCode());
    }
}
