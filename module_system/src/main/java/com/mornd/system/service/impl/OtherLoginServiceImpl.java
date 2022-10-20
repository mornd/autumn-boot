package com.mornd.system.service.impl;

import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.entity.dto.GiteeLoginUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.OtherLoginService;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public JsonResult loginByGitee(GiteeLoginUser user) {
        System.out.println(user);
        Map<String,Object> tokenMap = new HashMap<String, Object>(2) {{
            put("tokenHead", tokenProperties.getTokenHead());
            put("token", "");
        }};
        log.info("gitee用户{}登录系统成功", "");
        return JsonResult.success("登录成功", tokenMap);
    }
}
