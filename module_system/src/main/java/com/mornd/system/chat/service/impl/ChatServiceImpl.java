package com.mornd.system.chat.service.impl;

import com.mornd.system.chat.entity.ChatUser;
import com.mornd.system.chat.service.ChatService;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author: mornd
 * @dateTime: 2022/12/13 - 19:22
 */

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private UserService userService;

    @Override
    public TreeSet<ChatUser> users() {
        List<SysUser> userList = userService.list();
        // user 对象转为 chatUser 对象
        List<ChatUser> chatUsers =
                userList.stream().map(ChatUser::new).collect(Collectors.toList());

        // 按名称排序
        TreeSet<ChatUser> sortNameChatUsers =
                new TreeSet<>(Comparator.comparing(ChatUser::getName));
        sortNameChatUsers.addAll(chatUsers);
        return sortNameChatUsers;
    }
}
