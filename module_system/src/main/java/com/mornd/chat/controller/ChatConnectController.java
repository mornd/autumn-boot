package com.mornd.chat.controller;

import com.mornd.chat.entity.TransmissionChatMessage;
import com.mornd.chat.service.ChatService;
import com.mornd.system.constant.WebSocketConst;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysUser;
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
    private ChatService chatService;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 处理信息
     * @param authentication
     * @param chatMessage
     */
    @MessageMapping("/ws/chat")
    public void handleMessage(Authentication authentication, TransmissionChatMessage chatMessage) {
        // 这里有个bug，认证信息有时候为空
        if(authentication == null) {
            sendFailure(chatMessage, "消息发送失败，authentication为空");
            return;
        }
        AuthUser principal = (AuthUser) authentication.getPrincipal();
        SysUser sysUser = principal.getSysUser();
        chatMessage.setFrom(sysUser.getLoginName());
        chatMessage.setTo(chatMessage.getTo());
        chatMessage.setDate(LocalDateTime.now());
        chatMessage.setFromName(sysUser.getRealName());
        try {
            //  入库记录
            chatService.insertMessage(sysUser, chatMessage);
            chatMessage.setSuccess(true);
        } catch (Exception e) {
            sendFailure(chatMessage, e.getMessage());
            return;
        }

        if(chatMessage.getFrom().equals(chatMessage.getTo())) {
            // 自己发给自己消息
            return;
        }
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getTo(),
                WebSocketConst.CHAT_DESTINATION_PREFIXE + "/chat",
                chatMessage);
    }

    /**
     * 发送失败，这条消息发回给自己
     * @param chatMessage
     * @param message
     */
    private void sendFailure(TransmissionChatMessage chatMessage, String message) {
        chatMessage.setSuccess(false);
        chatMessage.setContent(message);
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getFrom(),
                WebSocketConst.CHAT_DESTINATION_PREFIXE + "/chat",
                chatMessage);
    }
}
