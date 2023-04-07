package com.mornd.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.chat.entity.*;
import com.mornd.chat.mapper.ChatMessageMapper;
import com.mornd.chat.mapper.ChatRecordMapper;
import com.mornd.chat.service.ChatService;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: mornd
 * @dateTime: 2022/12/13 - 19:22
 */

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private UserService userService;

    @Resource
    private ChatRecordMapper chatRecordMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Override
    public TreeSet<ChatUser> friends() {
        List<SysUser> userList = userService.list();
        // user 对象转为 chatUser 对象
        List<ChatUser> chatUsers =
                userList.stream().map(ChatUser::new).collect(Collectors.toList());

        // 按名称排序
        TreeSet<ChatUser> sortNameChatUsers =
                new TreeSet<>(Comparator.comparing(ChatUser::getLoginName));
        sortNameChatUsers.addAll(chatUsers);
        return sortNameChatUsers;
    }

    @Override
    public TreeSet<ChatUser> recentUsers() {
        // 获取聊天过的用户名
        SysUser loginUser = SecurityUtil.getLoginUser();
        Set<String> names = chatRecordMapper.getRecentUsername(loginUser.getLoginName());
        List<ChatUser> result = new ArrayList<>();
        names.forEach(name -> {
            SysUser sysUser = userService.getOne(
                    Wrappers.lambdaQuery(SysUser.class)
                            .eq(SysUser::getLoginName, name)
            );
            if(sysUser != null) {
                ChatUser chatUser = new ChatUser(sysUser);
                List<ChatRecord> lastRecord = chatRecordMapper.getRecord(loginUser.getLoginName(), name, true);
                Optional<ChatRecord> first = lastRecord.stream().findFirst();
                first.ifPresent((record) -> {
                    // 获取未读消息的总数
                    if(!loginUser.getLoginName().equals(name)) {
                        chatUser.setUnread(chatRecordMapper.unreadCount(loginUser.getLoginName(), name));
                    }
                    chatUser.setLastDate(record.getCreateTime());
                    chatUser.setLastMessage((record.getChatMessage() != null) ? record.getChatMessage().getContent() : "");
                    result.add(chatUser);
                });
            }
        });

        // 按最后聊天时间排序
        TreeSet<ChatUser> treeSet = new TreeSet<>((u1, u2) -> -u1.getLastDate().compareTo(u2.getLastDate()));
        treeSet.addAll(result);
        return treeSet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMessage(SysUser currentUser, TransmissionChatMessage transmissionChatMessage) {

        // 添加消息内容
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(transmissionChatMessage.getContent());
        chatMessageMapper.insert(chatMessage);

        //  添加消息记录
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setFromKey(transmissionChatMessage.getFrom());
        if(currentUser.getLoginName().equals(chatRecord.getFromKey())) {
            // 自己发给别人的消息默认已读
            chatRecord.setFromRead(1);
//            if(currentUser.getLoginName().equals(chatRecord.getToKey())) {
//                chatRecord.setToRead(1);
//            }
        }
        chatRecord.setToKey(transmissionChatMessage.getTo());
        chatRecord.setCreateTime(transmissionChatMessage.getDate());
        chatRecord.setMessageId(chatMessage.getId());
        chatRecordMapper.insert(chatRecord);
    }

    @Override
    public List<ChatSession> getSession(String other) {
        String loginUsername = SecurityUtil.getLoginUsername();
        List<ChatRecord> record = chatRecordMapper.getRecord(loginUsername, other, false);
        // 时间反转下
        Collections.reverse(record);
        List<ChatSession> result = record.stream().map(i -> new ChatSession(i, loginUsername)).collect(Collectors.toList());
        return result;
    }

    @Override
    @Async(value = "threadPoolTaskExecutor")
    public void read(String self, String other) {
        ChatRecord record = new ChatRecord();
        record.setToRead(1);
        LambdaUpdateWrapper<ChatRecord> uw = Wrappers.lambdaUpdate(ChatRecord.class);
        //uw.set(ChatRecord::getToRead, 1); 和上面的 record.setToRead(1); 效果一致
        uw.eq(ChatRecord::getFromKey, other);
        uw.eq(ChatRecord::getToKey, self);
        uw.eq(ChatRecord::getToRead, 0);
        // 第一个参数跟 UpdateWrapper.set 方法一样
        chatRecordMapper.update(record, uw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String other) {
        String loginUsername = SecurityUtil.getLoginUsername();
        Set<Long> recordDel = new HashSet<>();
        Set<Long> messageDel = new HashSet<>();

        LambdaUpdateWrapper<ChatRecord> qw = Wrappers.lambdaUpdate(ChatRecord.class);
        qw.eq(ChatRecord::getFromKey, loginUsername);
        qw.eq(ChatRecord::getToKey, other);
        qw.eq(ChatRecord::getFromDeleted, 0);
        List<ChatRecord> chatRecords = chatRecordMapper.selectList(qw);

        for (ChatRecord chatRecord : chatRecords) {
            if(1 == chatRecord.getToDeleted()) {
                recordDel.add(chatRecord.getId());
                messageDel.add(chatRecord.getMessageId());
            } else {
                LambdaUpdateWrapper<ChatRecord> uw = Wrappers.lambdaUpdate(ChatRecord.class);
                uw.set(ChatRecord::getFromDeleted, 1);
                uw.eq(ChatRecord::getId, chatRecord.getId());
                chatRecordMapper.update(null, uw);
            }
        }

        LambdaUpdateWrapper<ChatRecord> qw2 = Wrappers.lambdaUpdate(ChatRecord.class);
        qw2.eq(ChatRecord::getFromKey, other);
        qw2.eq(ChatRecord::getToKey, loginUsername);
        qw2.eq(ChatRecord::getToDeleted, 0);
        List<ChatRecord> chatRecords2 = chatRecordMapper.selectList(qw2);

        for (ChatRecord chatRecord : chatRecords2) {
            if(1 == chatRecord.getFromDeleted()) {
                recordDel.add(chatRecord.getId());
                messageDel.add(chatRecord.getMessageId());
            } else {
                LambdaUpdateWrapper<ChatRecord> uw = Wrappers.lambdaUpdate(ChatRecord.class);
                uw.set(ChatRecord::getToDeleted, 1);
                uw.eq(ChatRecord::getId, chatRecord.getId());
                chatRecordMapper.update(null, uw);
            }
        }

        // 删除表记录
        if(!recordDel.isEmpty()) {
            chatRecordMapper.deleteBatchIds(recordDel);
        }

        if(!messageDel.isEmpty()) {
            chatMessageMapper.deleteBatchIds(messageDel);
        }
    }

    @Override
    public void clearAll() {
        chatMessageMapper.clear();
        chatRecordMapper.clear();
    }
}
