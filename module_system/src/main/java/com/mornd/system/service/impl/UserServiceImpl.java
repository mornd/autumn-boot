package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.entity.AmqpMail;
import com.mornd.system.constants.MailConstants;
import com.mornd.system.config.AutumnConfig;
import com.mornd.system.constant.ResultMessage;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.entity.MailLog;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.entity.po.temp.UserWithRole;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.entity.vo.SysUserVO;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.mapper.MailLogMapper;
import com.mornd.system.mapper.UserMapper;
import com.mornd.system.mapper.UserWithRoleMapper;
import com.mornd.system.service.OnlineUserService;
import com.mornd.system.service.UploadService;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.AutumnUUID;
import com.mornd.system.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 15:56
 */

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {
    @Resource
    private UserWithRoleMapper userWithRoleMapper;
    @Resource
    private AuthUtil authUtil;
    @Resource
    private OnlineUserService onlineUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @Resource
    private UploadService uploadService;
    @Resource
    private AutumnConfig autumnConfig;
    @Resource
    private MailLogMapper mailLogMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;
    private Integer enabled = BaseEntity.EnableState.ENABLE.getCode();
    private Integer disabled = BaseEntity.EnableState.DISABLE.getCode();

    /**
     * ??????????????????
     * @param oldPwd
     * @return
     */
    @Override
    public boolean verifyCurrentPassword(String oldPwd) {
        String currentPwd = SecurityUtil.getLoginUser().getPassword();
        //matches() => ??????1??????????????????2?????????
        return passwordEncoder.matches(oldPwd, currentPwd);
    }

    /**
     * ????????????
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @Override
    public JsonResult changePwd(String oldPwd, String newPwd) {
        if(verifyCurrentPassword(oldPwd)) {
            SysUser user = new SysUser();
            user.setId(SecurityUtil.getLoginUserId());
            //???????????????
            user.setPassword(passwordEncoder.encode(newPwd));
            baseMapper.updateById(user);
            authUtil.delCacheLoginUser();
            return JsonResult.success(ResultMessage.UPDATE_MSG);
        }
        return JsonResult.failure("??????????????????");
    }

    /**
     * ????????????
     * @param user
     * @return
     */
    @Override
    public JsonResult pageList(SysUserVO user) {
        IPage<SysUserVO> page = new Page<>(user.getPageNo(), user.getPageSize());
        IPage<SysUserVO> userPage = baseMapper.pageList(page, user);
        return JsonResult.successData(userPage);
    }

    /**
     * ????????????
     * @param userVO
     * @return
     */
    @Override
    public List<SysUserVO> export(SysUserVO userVO) {
        List<SysUserVO> result = baseMapper.export(userVO);
        return result;
    }

    /**
     * ??????
     * @param userVO
     * @return
     */
    @Override
    public JsonResult insert(SysUserVO userVO) {
        if(this.queryLoginNameExists(userVO.getLoginName(), null)) throw new BadRequestException("??????????????????");
        if(queryPhoneExists(userVO.getPhone(), null)) throw new BadRequestException("?????????????????????");
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userVO, sysUser);
        sysUser.setId(null);
        //??????????????????
        sysUser.setPassword(passwordEncoder.encode(SecurityConst.USER_DEFAULT_PWD));
        sysUser.setCreateBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtCreate(new Date());
        baseMapper.insert(sysUser);

        //????????????
        if(ObjectUtils.isNotEmpty(userVO.getRoles())) {
            userVO.getRoles().forEach(id -> {
                UserWithRole uw = new UserWithRole();
                uw.setUserId(sysUser.getId());
                uw.setRoleId(id);
                uw.setGmtCreate(new Date());
                userWithRoleMapper.insert(uw);
            });
        }

        // ????????????
        if(autumnConfig.isUserMailNotification() && StringUtils.hasText(sysUser.getEmail())) {
            // ?????? id
            String msgId = AutumnUUID.fastUUID();

            CorrelationData correlationData = new CorrelationData(msgId);

            // ?????????????????????
            AmqpMail amqpMail = new AmqpMail();
            amqpMail.setMsgId(msgId);
            amqpMail.setUserId(sysUser.getId());
            amqpMail.setUsername(sysUser.getRealName());
            amqpMail.setLoginName(sysUser.getLoginName());
            amqpMail.setMail(sysUser.getEmail());
            amqpMail.setCreatedTime(new Date());

            // ????????????
            MailLog mailLog = new MailLog();
            mailLog.setMsgId(correlationData.getId());
            mailLog.setUserId(sysUser.getId());
            mailLog.setExchange(MailConstants.EXCHANGE_NAME);
            mailLog.setRoutingKey(MailConstants.ROUTING_KEY_NAME);
            mailLog.setStatus(MailLog.MailLogStatus.DELIVERING.ordinal());
            mailLog.setCreateTime(LocalDateTime.now());

            mailLogMapper.insert(mailLog);

            /**
             * ???????????????????????????????????????
             */
            correlationData.getFuture().addCallback(new SuccessCallback<CorrelationData.Confirm>() {
                @Override
                public void onSuccess(CorrelationData.Confirm confirm) {
                    if(confirm.isAck()) {
                        log.info("??????id???{}????????????????????????", correlationData.getId());
                    } else {
                        log.error("??????id???{}????????????????????????", correlationData.getId());

                        MailLog log = new MailLog(correlationData.getId(), MailLog.MailLogStatus.TO_EXCHANGE_ERROR.ordinal(),
                                "?????????????????????????????????" + confirm.getReason());
                        mailLogMapper.updateById(log);
                    }
                }
            }, new FailureCallback() {
                @Override
                public void onFailure(Throwable t) {
                    String errorMsg = t.getMessage();
                    log.error("??????id???{}?????????????????????{}", correlationData.getId(), errorMsg);

                    MailLog log = new MailLog(correlationData.getId(), MailLog.MailLogStatus.TO_EXCHANGE_ERROR.ordinal(),
                            "?????????????????????????????????" + errorMsg);
                    mailLogMapper.updateById(log);
                }
            });

            // ????????? amqpMail ???????????? Serializable ??????
            rabbitTemplate.convertAndSend(MailConstants.EXCHANGE_NAME,
                    MailConstants.ROUTING_KEY_NAME,
                    amqpMail,
                    correlationData);
        }

        return JsonResult.success("??????????????????????????????????????????");
    }

    /**
     * ???????????????????????????
     * @param user
     * @return
     */
    @Override
    public JsonResult update(SysUserVO user) {
        if(this.queryLoginNameExists(user.getLoginName(), user.getId())) throw new BadRequestException("??????????????????");
        if(queryPhoneExists(user.getPhone(), user.getId())) throw new BadRequestException("?????????????????????");
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        sysUser.setStatus(null);
        sysUser.setModifiedBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtModified(new Date());
        baseMapper.updateById(sysUser);

        //????????????
        LambdaQueryWrapper<UserWithRole> qw = Wrappers.lambdaQuery();
        qw.eq(UserWithRole::getUserId, user.getId());
        userWithRoleMapper.delete(qw);
        if(ObjectUtils.isNotEmpty(user.getRoles())) {
            user.getRoles().forEach(id -> {
                UserWithRole uw = new UserWithRole();
                uw.setUserId(sysUser.getId());
                uw.setRoleId(id);
                uw.setGmtCreate(new Date());
                userWithRoleMapper.insert(uw);
            });
        }

        if(SecurityUtil.getLoginUser().getId().equals(user.getId())) {
            // todo
            // ??????????????????
            authUtil.delCacheLoginUser();
        }
        return JsonResult.success();
    }

    /**
     * ??????????????????????????????
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResult userUpdate(SysUserVO user) {
        if(this.queryLoginNameExists(user.getLoginName(), user.getId())) throw new BadRequestException("??????????????????");
        if(queryPhoneExists(user.getPhone(), user.getId())) throw new BadRequestException("?????????????????????");
        // ????????????????????????
        String onlineUserKeyById = onlineUserService.getOnlineUserKeyById(user.getId());
        AuthUser principal = (AuthUser) SecurityUtil.getAuthentication().getPrincipal();
        SysUser cacheUser = principal.getSysUser();
        cacheUser.setLoginName(user.getLoginName());
        cacheUser.setRealName(user.getRealName());
        cacheUser.setGender(user.getGender());
        cacheUser.setPhone(user.getPhone());
        cacheUser.setEmail(user.getEmail());
        authUtil.updateAuthUser(onlineUserKeyById, principal);

        // ???????????????
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        sysUser.setStatus(null);
        sysUser.setGmtCreate(null);
        sysUser.setModifiedBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtModified(new Date());
        baseMapper.updateById(sysUser);
        authUtil.delCacheLoginUser();
        return JsonResult.success("??????????????????????????????");
    }

    /**
     * ??????????????????
     * @param user
     * @return
     */
    @Override
    public int updateAvatar(SysUser user) {
        LambdaUpdateWrapper<SysUser> uw = Wrappers.lambdaUpdate();
        uw.eq(SysUser::getId, user.getId());
        uw.set(SysUser::getAvatar, user.getAvatar());
        return baseMapper.update(null, uw);
    }

    /**
     * ??????
     * @param id
     * @return
     */
    @Override
    public JsonResult delete(String id) {
        //?????????????????????
        LambdaQueryWrapper<UserWithRole> qw = Wrappers.lambdaQuery();
        qw.eq(UserWithRole::getUserId, id);
        userWithRoleMapper.delete(qw);

        // ???????????????????????????
        LambdaQueryWrapper<SysUser> qw2 = Wrappers.lambdaQuery(SysUser.class);
        qw2.eq(SysUser::getId, id);
        SysUser sysUser = baseMapper.selectOne(qw2);
        //????????????
        baseMapper.deleteById(id);
        if(SecurityUtil.getLoginUserId().equals(id)) {
            authUtil.delCacheLoginUser();
        }
        // ????????????
        uploadService.deleteAvatar(sysUser.getAvatar());
        return JsonResult.success();
    }

    /**
     * ??????????????????
     * @param id
     * @param state
     * @return
     */
    @Override
    public JsonResult changeStatus(String id, Integer state) {
        //????????????
        if(!enabled.equals(state) && !disabled.equals(state)) {
            return JsonResult.failure("???????????????????????????");
        }
        LambdaUpdateWrapper<SysUser> uw = Wrappers.lambdaUpdate();
        uw.set(SysUser::getStatus, state);
        uw.eq(SysUser::getId, id);
        baseMapper.update(null, uw);
        if(SecurityUtil.getLoginUserId().equals(id)) {
            authUtil.delCacheLoginUser();
        }
        return JsonResult.success(ResultMessage.UPDATE_MSG);
    }

    /**
     * ???????????????????????????
     * @param name
     * @param id
     * @return
     */
    @Override
    public boolean queryLoginNameExists(String name, String id) {
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        qw.eq(SysUser::getLoginName, name);
        if(StrUtil.isNotBlank(id)) {
            qw.ne(SysUser::getId, id);
        }
        int count = baseMapper.selectCount(qw);
        return count > 0;
    }

    @Override
    public JsonResult getRoleById(String id) {
        Set<String> ids = baseMapper.getRoleById(id);
        return JsonResult.successData(ids);
    }

    @Override
    public SysUser getUserByPhone(String phone) {
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
        qw.eq(SysUser::getPhone, phone);
        return baseMapper.selectOne(qw);
    }

    /**
     * ??????????????????????????????
     * @param phone
     * @param id
     * @return
     */
    @Override
    public boolean queryPhoneExists(String phone, String id) {
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
        qw.eq(SysUser::getPhone, phone);
        qw.ne(StrUtil.isNotBlank(id), SysUser::getId, id);
        int count = baseMapper.selectCount(qw);
        return count > 0;
    }
}
