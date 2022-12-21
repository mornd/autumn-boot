package com.mornd.system.chat.service;

import com.mornd.system.chat.entity.ChatUser;

import java.util.TreeSet;

/**
 * @author: mornd
 * @dateTime: 2022/12/13 - 19:22
 */
public interface ChatService {
    TreeSet<ChatUser> users();
}
