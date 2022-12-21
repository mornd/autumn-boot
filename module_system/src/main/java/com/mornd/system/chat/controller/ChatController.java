package com.mornd.system.chat.controller;

import com.mornd.system.chat.entity.ChatUser;
import com.mornd.system.chat.service.ChatService;
import com.mornd.system.entity.result.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.TreeSet;

/**
 * @author: mornd
 * @dateTime: 2022/12/13 - 19:17
 */

@RequestMapping("/chat")
@RestController
public class ChatController {
    @Resource
    private ChatService chatService;

    /**
     * 查找所有聊天用户，并排除当前登录用户
     * @return
     */
    @GetMapping("/users")
    public JsonResult<?> users() {
        TreeSet<ChatUser> result = chatService.users();
        return JsonResult.successData(result);
    }

}
