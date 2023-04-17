package com.mornd.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mornd.process.entity.Process;
import com.mornd.process.entity.ProcessTemplate;
import com.mornd.process.service.ProcessTemplateService;
import com.mornd.process.service.WechatMessageService;
import com.mornd.system.config.AutumnConfig;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.AutumnException;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.mornd.process.entity.Process.ApproveStatus.AGREE;

/**
 * @author: mornd
 * @dateTime: 2023/4/12 - 23:57
 */

@Slf4j
@Service
public class WechatMessageServiceImpl implements WechatMessageService {
//    @Lazy 该注解可以解决与 processService 之间的循环依赖，不推荐
//    @Resource
//    private ProcessService processService;
    @Resource
    private UserService userService;
    @Resource
    private ProcessTemplateService processTemplateService;
    @Resource
    private AutumnConfig autumnConfig;
    @Resource
    private WxMpService wxMpService;

    @Override
    public void pushPendingMessage(Process process, String userId, String taskId) {
        // 流程模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        // 当前审批人
        SysUser user = userService.getById(userId);

        // 要推送的用户 openId
        String openId = user.getOpenId();
        if(!StringUtils.hasText(openId)) {
            //todo 做记录
            log.error("用户{}的openId为空，无法推送公众号消息", user.getRealName());
            return;
            //throw new AutumnException("用户" + user.getRealName() + "的openId为空，无法推送公众号消息");
        }

        // 流程提交人/发起人
        SysUser submitUser = userService.getById(process.getUserId());

        WxMpTemplateMessage template = WxMpTemplateMessage.builder()
                .toUser(openId)
                // 模板id wx开发文档获取
                .templateId("AVUxHqV6zinuDb3IbkcEyUhLDUaI7jVK5yPx6kG8ESY")
                // 用户点击模板后要访问的地址
                .url(autumnConfig.getOaUiBaseUrl() + "/#/show/" + process.getId() + "/" + taskId)
                .build();

        JSONObject form = JSON.parseObject(process.getFormValues());
        // formData 是表单v-model 英文值，formShowData是表单 label 中文值
        JSONObject formShowData = form.getJSONObject("formShowData");
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, Object> entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：")
                    .append(entry.getValue()).append("\n");
        }

        String color = "#272727";
        // 填充模板消息内容
        template.addData(
                new WxMpTemplateData("first",
                        user.getRealName() + "，你好！\n" + submitUser.getRealName() + "提交了\"" + processTemplate.getName() + "\"审批申请，请注意查看。", color));
        template.addData(
                new WxMpTemplateData("keyword1",
                        process.getProcessCode(), color));
        template.addData(
                new WxMpTemplateData("keyword2",
                        process.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), color));
        template.addData(
                new WxMpTemplateData("content",
                        content.toString(), color));

        try {
            // 开始推送
            String message = wxMpService.getTemplateMsgService().sendTemplateMsg(template);
            log.info("公众号消息推送完成，返回结果为：{}", message);
        } catch (WxErrorException e) {
            log.error("公众号消息推送失败：{}", e.getMessage());
            throw new AutumnException("公众号消息推送失败");
        }
    }

    @Override
    public void pushProcessedMessage(Process process, String userId, Integer status, String reason) {
        // 流程模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        // 提交人
        SysUser submitUser = userService.getById(userId);

        // 当前审批人
        SysUser currentUser = SecurityUtil.getLoginUser();

        // 要推送的用户 openId
        String openId = submitUser.getOpenId();
        if(!StringUtils.hasText(openId)) {
            log.error("用户{}的openId为空，无法推送公众号消息", submitUser.getRealName());
            return;
            //throw new AutumnException("用户" + submitUser.getRealName() + "的openId为空，无法推送公众号消息");
        }

        WxMpTemplateMessage template = WxMpTemplateMessage.builder()
                .toUser(openId)
                // 模板id wx开发文档获取
                .templateId("dn_yVajNYxUzWHn-ekofwgDmyDHLYnQ983j3i3edMB8")
                // 用户点击模板后要访问的地址 参数0只是为了占位
                .url(autumnConfig.getOaUiBaseUrl() + "/#/show/" + process.getId() + "/0")
                .build();

        JSONObject form = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = form.getJSONObject("formShowData");
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, Object> entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：")
                    .append(entry.getValue()).append("\n");
        }

        String color = "#272727";
        // 填充模板消息内容
        template.addData(
                new WxMpTemplateData("first",
                        submitUser.getRealName() + "，你好！\n" + "你发起的\"" + processTemplate.getName() + "\"审批申请已经被处理了，请注意查看。", color));
        template.addData(
                new WxMpTemplateData("keyword1",
                        process.getProcessCode(), color));
        template.addData(
                new WxMpTemplateData("keyword2",
                        process.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), color));
        template.addData(
                new WxMpTemplateData("keyword3",
                        currentUser.getRealName(), color));

        // 驳回给出原因
        String result = AGREE.getCode().equals(status) ? "通过" : "驳回\n驳回原因：" + reason;
        template.addData(
                new WxMpTemplateData("keyword4",
                        result, color));
        template.addData(
                new WxMpTemplateData("content",
                        content.toString(), color));

        try {
            // 开始推送
            String message = wxMpService.getTemplateMsgService().sendTemplateMsg(template);
            log.info("公众号消息推送完成，返回结果为：{}", message);
        } catch (WxErrorException e) {
            log.error("公众号消息推送失败：{}", e.getMessage());
            throw new AutumnException("公众号消息推送失败");
        }
    }
}
