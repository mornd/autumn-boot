package com.mornd.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.entity.dto.OtherLoginUseDTO;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.OtherLoginService;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 0:35
 */
@Slf4j
@Service
public class OtherLoginServiceImpl implements OtherLoginService {
    @Resource
    private TokenProperties tokenProperties;

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
        String uuid = IdUtil.fastUUID();
        String authorizeUrl = authRequest.authorize(uuid);
        Map<String,Object> map = new HashMap<>();
        map.put("authorizeUrl", authorizeUrl);
        map.put("uuid", uuid);
        return JsonResult.successData(map);
    }

    @Override
    public JsonResult loginByGitee(OtherLoginUseDTO user) {
        final String uuid = user.getUuid();
        final String code = user.getCode();
        final String source =user.getSource();

        AuthGiteeRequest authGiteeRequest =
                new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(clientId).clientSecret(secret).redirectUri(uri).build());
        // 执行登录
        AuthResponse<AuthUser> loginUser = authGiteeRequest
                .login(AuthCallback.builder().state(uuid).code(code).build());

        System.out.println(loginUser);
        // 头像，配置文件
        // https://portrait.gitee.com/uploads/avatars/user/2617/7853024_mornd_1608529482.png
        Map<String,Object> tokenMap = new HashMap<String, Object>(2) {{
            put("tokenHead", tokenProperties.getTokenHead());
            put("token", "");
        }};
        log.info("gitee用户{}登录系统成功", "");
        return JsonResult.success("登录成功", tokenMap);
    }
}
