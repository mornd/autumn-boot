package com.mornd.mail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.mail.entity.MailLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: mornd
 * @dateTime: 2023/2/9 - 21:25
 */

@Mapper
public interface MailLogMapper extends BaseMapper<MailLog> {
}
