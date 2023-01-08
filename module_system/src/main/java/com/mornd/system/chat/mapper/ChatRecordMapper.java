package com.mornd.system.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.chat.entity.ChatRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author: mornd
 * @dateTime: 2022/12/24 - 19:38
 */

@Mapper
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {
    Set<String> getRecentUsername(@Param("loginName") String loginName);

    List<ChatRecord> getRecord(@Param("self") String self, @Param("other") String other, @Param("last") boolean last);

    int unreadCount(@Param("self") String self, @Param("other") String other);

    @Select("TRUNCATE TABLE chat_record")
    void clear();
}
