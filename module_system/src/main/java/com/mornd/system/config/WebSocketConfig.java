package com.mornd.system.config;

import com.mornd.system.config.security.components.TokenProperties;
import com.mornd.system.constant.WebSocketConst;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;

/**
 * @author: mornd
 * @dateTime: 2022/12/12 - 23:01
 * @description: websocket 配置
 */

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Resource
    private TokenProperties tokenProperties;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private AuthUtil authUtil;

    /**
     * 添加 endpoint，使网页可以通过 websocket 连接该服务
     * 也可以指定是否使用 sockJS
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * 将路径注册为 stomp 的端点，用户连接这个端点就可以进行通讯
         * setAllowedOrigins 允许跨域
         * withSockJS 支持 sockjs 访问
         */
        registry.addEndpoint(WebSocketConst.CHAT_END_POINT).setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * 输入管道配置
     * 该方法不使用 jwt 令牌机制可不用配置
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                assert accessor != null;
                // 判断是否为连接，如果是，需要获取 token，并设置用户对象
                if(StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader(tokenProperties.getTokenHeader());
                    token = getToken(token);
                    if(StringUtils.hasText(token)) {
                        // 查找缓存数据
                        AuthUser authUser = (AuthUser) redisUtil.getValue(authUtil.getLoginUserRedisKey(token));
                        if(authUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            UsernamePasswordAuthenticationToken authenticationToken
                                    = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                            accessor.setUser(authenticationToken);
                            log.info("webSocket连接建立成功");
                        }
                    }
                }
                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置代理域，可配置多个
        registry.enableSimpleBroker(WebSocketConst.CHAT_DESTINATION_PREFIXE);
    }

    /**
     * 获取 token 的值
     * @param token
     * @return
     */
    private String getToken(final String token) {
        if(StringUtils.hasText(token) && token.startsWith(tokenProperties.getTokenHead())) {
            return  token.replace(tokenProperties.getTokenHead(), "");
        } else {
            return null;
        }
    }
}
