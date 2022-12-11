package com.mornd.system.service.impl;

import cn.hutool.core.io.FileUtil;
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
import com.mornd.system.utils.*;
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
        SysUser user = userService.getOne(qw);
        if(user == null) throw new BadRequestException("用户不存在");
        String url = null;

        if(UploadStorageType.QINIU.getCode().equals(autumnConfig.getUploadStorage())) {
            // 七牛云
            url = qiniuUtil.upload(file.getInputStream(), file.getOriginalFilename());
            url += "http://";
        } else if(UploadStorageType.ALIYUN.getCode().equals(autumnConfig.getUploadStorage())) {
            //  阿里云oss
            url = AliyunOssUtil.upload(file.getInputStream(), file.getOriginalFilename());
        } else {
            // 本地磁盘
            // D:/autumn/uploadPath/avatar
            String avatarPath = autumnConfig.getProfile() + File.separator + GlobalConst.AVATAR_DIR_NAME;
            MyFileUtil.mkdirs(avatarPath);

            // /ad5fdd7c-b601-4c28-bcc1-10942fe59af5.jpg
            String resourcePath = File.separator + MyIdUtil.fastUUID() + MyFileUtil.getFileSuffix(file);
            try {
                file.transferTo(new File(avatarPath + resourcePath));
            } catch (IOException e) {
                e.printStackTrace();
                throw new BadRequestException("头像上传发生异常，请重试");
            }
            // /profile/avatar/011f1c31-4031-463d-8b1d-64986579bda4.jpg
            url = String.format("%s%s%s%s", GlobalConst.RESOURCE_PREFIX,
                    File.separator, GlobalConst.AVATAR_DIR_NAME, resourcePath);
        }
        if(!StringUtils.hasText(url)) {
            throw new BadRequestException("头像上传失败，请重试");
        }

        // 删除之前的头像
        this.deleteAvatar(user.getAvatar());

        if(id.equals(SecurityUtil.getLoginUserId())) {
            // 更新缓存中的用户信息
            String key =  onlineUserService.getOnlineUserKeyById(id);
            AuthUser principal = (AuthUser) SecurityUtil.getAuthentication().getPrincipal();
            principal.getSysUser().setAvatar(url);
            authUtil.updateAuthUser(key, principal);
        }

        // 更新数据库
        user.setAvatar(url);
        userService.updateAvatar(user);
        return url;
    }

    /**
     * 删除头像文件
     * @param path
     */
    @Override
    public void deleteAvatar(String path) {
        if(!StringUtils.hasText(path)) {
            return;
        }
        if(UploadStorageType.QINIU.getCode().equals(autumnConfig.getUploadStorage())) {
            qiniuUtil.delete(path.substring(path.lastIndexOf("/") + 1));
        } else if(UploadStorageType.ALIYUN.getCode().equals(autumnConfig.getUploadStorage())) {
            AliyunOssUtil.delete(path);
        } else {
            if(path.startsWith(GlobalConst.RESOURCE_PREFIX)) {
                path = path.replace(GlobalConst.RESOURCE_PREFIX, "");
            }
            path = autumnConfig.getProfile() + path;
            FileUtil.del(path);
        }
    }
}
