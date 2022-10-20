package com.mornd.system.controller;

import cn.hutool.core.util.IdUtil;
import com.mornd.system.annotation.Anonymous;
import com.mornd.system.entity.dto.GiteeLoginUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.OtherLoginService;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGiteeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mornd
 * @dateTime 2022/10/19 - 22:11
 * 其它登录方式处理
 */
@Anonymous
@RestController
public class OtherLoginController {
    @Resource
    private OtherLoginService otherLoginService;

    // 登录gitee，点击设置-第三方应用中申请
    @Value("${otherLogin.gitee.clientId}")
    private String clientId;
    @Value("${otherLogin.gitee.secret}")
    private String secret;
    @Value("${otherLogin.gitee.uri}")
    private String uri;

    /**
     * gitee 方式登录
     * @return
     */
    @GetMapping("/preLoginByGitee")
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

    @PostMapping("/loginByGitee")
    public JsonResult loginByGitee(@RequestBody GiteeLoginUser user) {
        return otherLoginService.loginByGitee(user);
    }
}
