package com.mornd.system.chat.controller;

import com.mornd.system.chat.entity.ChatMessage;
import com.mornd.system.constant.WebSocketConst;
import com.mornd.system.entity.dto.AuthUser;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author: mornd
 * @dateTime: 2022/12/12 - 23:53
 */

@Controller
public class ChatConnectController {

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 处理信息
     * @param authentication
     * @param chatMessage
     */
    @MessageMapping("/ws/chat")
    public void handleMessage(Authentication authentication, ChatMessage chatMessage) {
        AuthUser principal = (AuthUser) authentication.getPrincipal();
        chatMessage.setFrom(principal.getSysUser().getLoginName());
        chatMessage.setTo(chatMessage.getTo());
        chatMessage.setDate(LocalDateTime.now());
        chatMessage.setFromName(principal.getSysUser().getRealName());
        // todo 入库记录
        if(chatMessage.getFrom().equals(chatMessage.getTo())) {
            // 自己发给自己消息
            return;
        }
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getTo(),
                WebSocketConst.CHAT_DESTINATION_PREFIXE + "/chat",
                chatMessage);
    }
}