package com.mornd.chat.service;

import com.mornd.chat.entity.ChatSession;
import com.mornd.chat.entity.ChatUser;
import com.mornd.chat.entity.TransmissionChatMessage;
import com.mornd.system.entity.po.SysUser;

import java.util.List;
import java.util.TreeSet;

/**
 * @author: mornd
 * @dateTime: 2022/12/13 - 19:22
 */
public interface ChatService {
    TreeSet<ChatUser> friends();

    public void insertMessage(SysUser currentUser, TransmissionChatMessage transmissionChatMessage);

    TreeSet<ChatUser> recentUsers();

    List<ChatSession> getSession(String other);

    void read(String self, String other);

    void delete(String other);

    void clearAll();
}
