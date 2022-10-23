package com.mornd.system.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.system.config.AutumnConfig;
import com.mornd.system.constant.GlobalConst;
import com.mornd.system.constant.enums.UploadStorageType;
import com.mornd.system.entity.dto.AuthUser;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.service.OnlineUserService;
import com.mornd.system.service.UploadService;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.AuthUtil;
import com.mornd.system.utils.MyFileUtil;
import com.mornd.system.utils.QiniuUtil;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author mornd
 * @dateTime 2022/10/22 - 20:16
 */
@Service
public class UploadServiceImpl implements UploadService {

    @Resource
    private QiniuUtil qiniuUtil;
    @Resource
    private UserService userService;
    @Resource
    private AutumnConfig autumnConfig;
    @Resource
    private OnlineUserService onlineUserService;
    @Resource
    private AuthUtil authUtil;

    @Override
    public String uploadAvatar(String id, MultipartFile file) throws Exception {
        //查询用户之前的头像
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        qw.eq(SysUser::getId, id);
        qw.last("LIMIT 1");
        SysUser user = userService.getOne(qw);
        String url = null;

        if(UploadStorageType.QINIU.getCode().equals(autumnConfig.getUploadStorage())) {
            // 七牛云
            url = qiniuUtil.upload(file.getInputStream(), file.getOriginalFilename());
            if(!StringUtils.hasText(url)) {
                throw new BadRequestException("头像上传失败，请重试");
            }
            url += "http://";
            //删除之前的头像
            String avatar = user.getAvatar();
            if(StrUtil.isNotBlank(avatar)) {
                qiniuUtil.delete(avatar.substring(avatar.lastIndexOf("/") + 1));
            }
        } else {
            // 本地磁盘
            // D:/autumn/uploadPath/avatar
            String avatarPath = autumnConfig.getProfile() + File.separator + GlobalConst.AVATAR_DIR_NAME;
            MyFileUtil.mkdirs(avatarPath);

            // /ad5fdd7c-b601-4c28-bcc1-10942fe59af5.jpg
            String resourcePath = File.separator + IdUtil.fastUUID() + MyFileUtil.getFileSuffix(file);
            try {

                file.transferTo(new File(avatarPath + resourcePath));
            } catch (IOException e) {
                e.printStackTrace();
                throw new BadRequestException("头像上传发生异常，请重试");
            }

            // 删除之前的头像
            String oldAvatar= user.getAvatar();
            if(StringUtils.hasText(oldAvatar)) {
                if(oldAvatar.startsWith(GlobalConst.RESOURCE_PREFIX)) {
                    oldAvatar = oldAvatar.replace(GlobalConst.RESOURCE_PREFIX, "");
                }
                oldAvatar = autumnConfig.getProfile() + oldAvatar;
                FileUtil.del(oldAvatar);
            }
            // /profile/avatar/011f1c31-4031-463d-8b1d-64986579bda4.jpg
            url = String.format("%s%s%s%s", GlobalConst.RESOURCE_PREFIX,
                    File.separator, GlobalConst.AVATAR_DIR_NAME, resourcePath);
        }

        if(id.equals(SecurityUtil.getLoginUserId())) {
            // 更新缓存中的用户信息
            String key =  onlineUserService.getOnlineUserKeyById(id);
            AuthUser principal = (AuthUser) SecurityUtil.getAuthentication().getPrincipal();
            principal.getSysUser().setAvatar(url);
            authUtil.updateAuthUser(key,principal);
        }

        // 更新数据库
        user.setAvatar(url);
        userService.updateAvatar(user);
        return url;
    }
}
