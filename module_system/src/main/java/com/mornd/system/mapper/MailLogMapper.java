package com.mornd.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mornd.system.entity.po.MailLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: mornd
 * @dateTime: 2023/2/4 - 13:07
 */

@Mapper
public interface MailLogMapper extends BaseMapper<MailLog> {
}
