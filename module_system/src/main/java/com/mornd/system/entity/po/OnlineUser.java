package com.mornd.system.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mornd.system.entity.dto.AuthUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author mornd
 * @dateTime 2022/10/20 - 19:39
 */
@Data
@NoArgsConstructor
public class OnlineUser {
    public OnlineUser(AuthUser authUser) {
        loginName = authUser.getSysUser().getLoginName();
        realName = authUser.getSysUser().getRealName();
        loginTime = authUser.getLoginTime();
        ip = authUser.getIp();
        address = authUser.getAddress();
        os = authUser.getOs();
        browser = authUser.getBrowser();
        duration = (System.currentTimeMillis() - loginTime.getTime()) / 1000;
    }

    private String loginName;
    private String realName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    private String ip;
    private String address;
    private String os;
    private String browser;
    private Long duration;
    @NotNull(message = "分页信息的起始页不能为空")
    private Integer pageNo;
    @NotNull(message = "分页信息的每页个数不能为空")
    private Integer pageSize;
}
