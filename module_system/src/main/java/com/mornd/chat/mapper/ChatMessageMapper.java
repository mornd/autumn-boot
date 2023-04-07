package com.mornd.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author: mornd
 * @dateTime: 2022/12/24 - 19:38
 */

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    @Update("TRUNCATE TABLE chat_message")
    void clear();
}
