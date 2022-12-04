package com.mornd.system.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.mornd.system.constant.AliyunOssConstant;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: mornd
 * @dateTime: 2022/12/4 - 17:37
 * @description: 阿里云短信服务
 */

@Slf4j
@Component
public class AliyunPhoneMsgUtil {

    /**
     * 验证码有效期/分钟
     */
    public static final Integer CODE_TIME_OUT = 5;

    /**
     * 是否只发送短信给本系统的用户
     */
    public static final boolean SEND_SYS_USER = false;

    @Resource
    private UserService userService;


    /**
     * 调用阿里云 API 发送短信验证码
     * @param phone 手机号码
     * @param code 验证码
     */
    public boolean clientSendMsg(String phone, String code) {
        if(SEND_SYS_USER) {
            SysUser sysUser = userService.getUserByPhone(phone);
            if(sysUser == null) throw new BadRequestException("该手机号码" + phone + "在本系统中不存在");
        }
        // 转换为阿里云要求的 json 参数格式
        Map<String, String> params = new HashMap<>();
        params.put("code", code);

        // 配置，用到的 accesskey 和对象存储是同一个
        DefaultProfile profile = DefaultProfile.getProfile("default",
                AliyunOssConstant.ACCESS_KEY_ID,
                AliyunOssConstant.ACCESS_KEY_SECRET);
        DefaultAcsClient client = new DefaultAcsClient(profile);

        // 构建请求
        CommonRequest request = new CommonRequest();
        // 发送协议
        request.setSysProtocol(ProtocolType.HTTPS);
        // 发送方式
        request.setSysMethod(MethodType.POST);
        // 短信API产品域名（接口地址固定，无需修改）
        request.setSysDomain("dysmsapi.aliyuncs.com");
        // 阿里云定义的版本号时间，不可省略
        request.setSysVersion("2017-05-25");
        // 阿里云定义的api名称，不可省略
        request.setSysAction("SendSms");

        // 发送的手机号
        request.putQueryParameter("PhoneNumbers", phone);
        // 对应阿里云控制台的签名名称
        request.putQueryParameter("SignName", "我的autumn办公系统");
        // 对应阿里云控制台的模板 code
        request.putQueryParameter("TemplateCode", "SMS_262465415");
        // 验证码转换参数转换
        request.putQueryParameter("TemplateParam", new Gson().toJson(params));

        try {
            // 发送并响应结果
            CommonResponse response = client.getCommonResponse(request);
            // {"Message":"OK","RequestId":"3D44243A-AFE6-5698-8767-26E72CCB4C30","Code":"OK","BizId":"136807270143518125^0"}
            log.info("调用阿里云短信API返回结果：" + response.getData());
            boolean success = response.getHttpResponse().isSuccess();
            return success;
        } catch (ClientException e) {
            log.error(e.getErrMsg());
            throw new BadRequestException("短信发送失败，" + e.getErrMsg());
        }
    }
}
