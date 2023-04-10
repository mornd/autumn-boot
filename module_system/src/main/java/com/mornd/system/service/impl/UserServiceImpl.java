package com.mornd.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.mail.entity.AmqpMail;
import com.mornd.mail.constants.MailConstants;
import com.mornd.system.config.AutumnConfig;
import com.mornd.system.constant.SecurityConst;
import com.mornd.mail.entity.MailLog;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.po.temp.UserWithRole;
import com.mornd.system.entity.vo.SysUserVO;
import com.mornd.system.exception.AutumnException;
import com.mornd.system.exception.BadRequestException;
import com.mornd.mail.mapper.MailLogMapper;
import com.mornd.system.mapper.UserMapper;
import com.mornd.system.mapper.UserWithRoleMapper;
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

import static com.mornd.system.entity.po.base.BaseEntity.EnableState.DISABLE;
import static com.mornd.system.entity.po.base.BaseEntity.EnableState.ENABLE;

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
    private BCryptPasswordEncoder passwordEncoder;
    @Resource
    private UploadService uploadService;
    @Resource
    private AutumnConfig autumnConfig;
    @Resource
    private MailLogMapper mailLogMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 验证当前密码
     * @param oldPwd
     * @return
     */
    @Override
    public boolean verifyCurrentPassword(String oldPwd) {
        String currentPwd = SecurityUtil.getLoginUser().getPassword();
        //matches() => 参数1：明文，参数2：密文
        return passwordEncoder.matches(oldPwd, currentPwd);
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     */
    @Override
    public void changePwd(String oldPwd, String newPwd) {
        if(verifyCurrentPassword(oldPwd)) {
            SysUser user = new SysUser();
            user.setId(SecurityUtil.getLoginUserId());
            //加密新密码
            user.setPassword(passwordEncoder.encode(newPwd));
            baseMapper.updateById(user);
            authUtil.deleteCacheUser();
        } else {
            throw new AutumnException("原密码不匹配");
        }
    }

    /**
     * 分页查询
     * @param user
     * @return
     */
    @Override
    public IPage<SysUserVO> pageList(SysUserVO user) {
        IPage<SysUserVO> page = new Page<>(user.getPageNo(), user.getPageSize());
        baseMapper.pageList(page, user);
        return page;
    }

    /**
     * 导出数据
     * @param userVO
     * @return
     */
    @Override
    public List<SysUserVO> export(SysUserVO userVO) {
        List<SysUserVO> result = baseMapper.export(userVO);
        return result;
    }

    /**
     * 新增
     * @param userVO
     */
    @Override
    public void insert(SysUserVO userVO) {
        if(this.queryLoginNameExists(userVO.getLoginName(), null)) {
            throw new BadRequestException("登录名已重复");
        }
        if(StringUtils.hasText(userVO.getPhone())
            && queryPhoneExists(userVO.getPhone(), null)) {
            throw new BadRequestException("手机号码已重复");
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userVO, sysUser);
        sysUser.setId(null);
        //设置默认密码
        sysUser.setPassword(passwordEncoder.encode(SecurityConst.USER_DEFAULT_PWD));
        sysUser.setCreateBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtCreate(new Date());
        baseMapper.insert(sysUser);

        //角色相关
        if(ObjectUtils.isNotEmpty(userVO.getRoles())) {
            userVO.getRoles().forEach(id -> {
                UserWithRole uw = new UserWithRole();
                uw.setUserId(sysUser.getId());
                uw.setRoleId(id);
                uw.setGmtCreate(new Date());
                userWithRoleMapper.insert(uw);
            });
        }

        // 邮件通知
        if(autumnConfig.isUserMailNotification() && StringUtils.hasText(sysUser.getEmail())) {
            // 消息 id
            String msgId = AutumnUUID.fastUUID();

            CorrelationData correlationData = new CorrelationData(msgId);

            // 发送的消息实体
            AmqpMail amqpMail = new AmqpMail();
            amqpMail.setMsgId(msgId);
            amqpMail.setUserId(sysUser.getId());
            amqpMail.setUsername(sysUser.getRealName());
            amqpMail.setLoginName(sysUser.getLoginName());
            amqpMail.setMail(sysUser.getEmail());
            amqpMail.setCreatedTime(new Date());

            // 邮件日志
            MailLog mailLog = new MailLog();
            mailLog.setMsgId(correlationData.getId());
            mailLog.setUserId(sysUser.getId());
            mailLog.setExchange(MailConstants.EXCHANGE_NAME);
            mailLog.setRoutingKey(MailConstants.ROUTING_KEY_NAME);
            mailLog.setStatus(MailLog.MailLogStatus.DELIVERING.ordinal());
            mailLog.setCreateTime(LocalDateTime.now());

            mailLogMapper.insert(mailLog);

            /**
             * 这里单独配置消息失败的策略
             */
            correlationData.getFuture().addCallback(new SuccessCallback<CorrelationData.Confirm>() {
                @Override
                public void onSuccess(CorrelationData.Confirm confirm) {
                    if(confirm.isAck()) {
                        log.info("消息id：{}投递到交换机成功", correlationData.getId());
                    } else {
                        log.error("消息id：{}投递到交换机失败", correlationData.getId());

                        MailLog log = new MailLog(correlationData.getId(), MailLog.MailLogStatus.TO_EXCHANGE_ERROR.ordinal(),
                                "消息投递到交换机失败：" + confirm.getReason());
                        mailLogMapper.updateById(log);
                    }
                }
            }, new FailureCallback() {
                @Override
                public void onFailure(Throwable t) {
                    String errorMsg = t.getMessage();
                    log.error("消息id：{}投递发生异常：{}", correlationData.getId(), errorMsg);

                    MailLog log = new MailLog(correlationData.getId(), MailLog.MailLogStatus.TO_EXCHANGE_ERROR.ordinal(),
                            "消息投递到交换机失败：" + errorMsg);
                    mailLogMapper.updateById(log);
                }
            });

            // 实体类 amqpMail 必须实现 Serializable 接口
            rabbitTemplate.convertAndSend(MailConstants.EXCHANGE_NAME,
                    MailConstants.ROUTING_KEY_NAME,
                    amqpMail,
                    correlationData);
        }
    }

    /**
     * 管理员修改用户信息
     * @param user
     */
    @Override
    public void update(SysUserVO user) {
        if(this.queryLoginNameExists(user.getLoginName(), user.getId())) {
            throw new BadRequestException("登录名已重复");
        }
        if(StringUtils.hasText(user.getPhone())
            && queryPhoneExists(user.getPhone(), user.getId())) {
            throw new BadRequestException("手机号码已重复");
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        sysUser.setStatus(null);
        sysUser.setModifiedBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtModified(new Date());
        baseMapper.updateById(sysUser);

        //角色相关
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
        // 删除缓存信息
        authUtil.deleteCacheUser(user.getId());
    }

    /**
     * 用户自己修改个人信息
     * @param user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userUpdate(SysUserVO user) {
        if(this.queryLoginNameExists(user.getLoginName(), user.getId())) {
            throw new BadRequestException("登录名已重复");
        }
        if(StringUtils.hasText(user.getPhone())
                && queryPhoneExists(user.getPhone(), user.getId())) {
            throw new BadRequestException("手机号码已重复");
        }
        AuthUser principal = (AuthUser) SecurityUtil.getAuthentication().getPrincipal();
        SysUser cacheUser = principal.getSysUser();
        cacheUser.setLoginName(user.getLoginName());
        cacheUser.setRealName(user.getRealName());
        cacheUser.setGender(user.getGender());
        cacheUser.setPhone(user.getPhone());
        cacheUser.setEmail(user.getEmail());
        // 更新缓存中的用户
        authUtil.updateCacheUser(user.getId(), principal);

        // 更新数据库
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        sysUser.setStatus(null);
        sysUser.setGmtCreate(null);
        sysUser.setModifiedBy(SecurityUtil.getLoginUserId());
        sysUser.setGmtModified(new Date());
        baseMapper.updateById(sysUser);
    }

    /**
     * 用户修改头像
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
     * 删除
     * @param id
     */
    @Override
    public void delete(String id) {
        //删除关系表数据
        LambdaQueryWrapper<UserWithRole> qw = Wrappers.lambdaQuery();
        qw.eq(UserWithRole::getUserId, id);
        userWithRoleMapper.delete(qw);

        // 获取用户的头像地址
        LambdaQueryWrapper<SysUser> qw2 = Wrappers.lambdaQuery(SysUser.class);
        qw2.eq(SysUser::getId, id);
        SysUser sysUser = baseMapper.selectOne(qw2);
        //执行删除
        baseMapper.deleteById(id);
        // 删除头像
        uploadService.deleteAvatar(sysUser.getAvatar());
        // 删除缓存中的用户
        authUtil.deleteCacheUser(id);
    }

    /**
     * 修改启用状态
     * @param id
     * @param state
     */
    @Override
    public void changeStatus(String id, Integer state) {
        //参数校验
        if(!ENABLE.getCode().equals(state) && !DISABLE.getCode().equals(state)) {
            throw new AutumnException("修改的状态值不正确");
        }
        LambdaUpdateWrapper<SysUser> uw = Wrappers.lambdaUpdate();
        uw.set(SysUser::getStatus, state);
        uw.eq(SysUser::getId, id);
        baseMapper.update(null, uw);
        // 删除缓存用户
        authUtil.deleteCacheUser(id);
    }

    /**
     * 查询登录名是否重复
     * @param name
     * @param id
     * @return
     */
    @Override
    public boolean queryLoginNameExists(String name, String id) {
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        qw.eq(SysUser::getLoginName, name);
        if(StringUtils.hasText(id)) {
            qw.ne(SysUser::getId, id);
        }
        int count = baseMapper.selectCount(qw);
        return count > 0;
    }

    @Override
    public Set<String> getRoleById(String id) {
        Set<String> ids = baseMapper.getRoleById(id);
        return ids;
    }

    @Override
    public SysUser getUserByPhone(String phone) {
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
        qw.eq(SysUser::getPhone, phone);
        return baseMapper.selectOne(qw);
    }

    /**
     * 查询电话号码是否存在
     * @param phone
     * @param id
     * @return
     */
    @Override
    public boolean queryPhoneExists(String phone, String id) {
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class);
        qw.eq(SysUser::getPhone, phone);
        qw.ne(StringUtils.hasText(id), SysUser::getId, id);
        int count = baseMapper.selectCount(qw);
        return count > 0;
    }
}
