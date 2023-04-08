package com.mornd.process.config;

import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: mornd
 * @dateTime: 2023/4/8 - 16:21
 */

@Configuration
@RequiredArgsConstructor
public class WechatMpConfig {
    private final WechatAccountConfig accountConfig;

    @Bean
    public WxMpService wxMpService() {
        WxMpServiceImpl wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }

    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpDefaultConfigImpl wxMpDefaultConfig = new WxMpDefaultConfigImpl();
        wxMpDefaultConfig.setAppId(accountConfig.getMpAppId());
        wxMpDefaultConfig.setSecret(accountConfig.getMpAppSecret());
        return wxMpDefaultConfig;
    }
}
