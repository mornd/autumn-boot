package com.mornd.system.chat.controller;

import com.mornd.system.chat.entity.ChatSession;
import com.mornd.system.chat.entity.ChatUser;
import com.mornd.system.chat.service.ChatService;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
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
     * 查找所有 chat 好友，并按账号名称排序
     * @return
     */
    @GetMapping("/allFriends")
    public JsonResult<?> friends() {
        TreeSet<ChatUser> result = chatService.friends();
        return JsonResult.successData(result);
    }

    /**
     * 获取最近聊天的好友
     * @return
     */
    @GetMapping("/getRecentUsers")
    public JsonResult<?> recentUsers() {
        TreeSet<ChatUser> reslut = chatService.recentUsers();
        return JsonResult.successData(reslut);
    }

    /**
     * 获取与某个好友的聊天消息
     * @param other
     * @return
     */
    @GetMapping("/getSession/{other}")
    public JsonResult<?> getSession(@PathVariable String other) {
        List<ChatSession> result = chatService.getSession(other);
        return JsonResult.successData(result);
    }

    /**
     * 用户已读取全部消息
     * @param other 聊天对象
     */
    @PutMapping("/read/{other}")
    public void read(@PathVariable String other) {
        chatService.read(SecurityUtil.getLoginUsername(), other);
    }

    /**
     * 删除与某人的聊天记录
     * @param other
     */
    @DeleteMapping("/delete/{other}")
    public JsonResult delete(@PathVariable String other) {
        chatService.delete(other);
        return JsonResult.success();
    }

    /**
     * 超级管理员清除所有的聊天记录
     */
    @PreAuthorize("hasRole('super_admin')")
    @DeleteMapping("/clear")
    public JsonResult clearAll() {
        chatService.clearAll();
        return JsonResult.success();
    }
}
