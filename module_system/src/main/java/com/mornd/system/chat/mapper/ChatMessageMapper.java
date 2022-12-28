package com.mornd.system.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: mornd
 * @dateTime: 2022/12/24 - 19:38
 */

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
