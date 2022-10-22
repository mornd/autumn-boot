package com.mornd.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.service.UploadService;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.QiniuUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

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

    @Override
    public String uploadAvatar(String id, MultipartFile file) throws Exception {
        //查询用户之前的头像
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        qw.eq(SysUser::getId, id);
        qw.last("LIMIT 1");
        SysUser user = userService.getOne(qw);

        String url = qiniuUtil.upload(file.getInputStream(), file.getOriginalFilename());
        if(url == null) {
            throw new BadRequestException("上传失败，请重试");
        }
        url = "http://" + url;
        //删除之前的头像
        String avatar = user.getAvatar();
        if(StrUtil.isNotBlank(avatar)) {
            qiniuUtil.delete(avatar.substring(avatar.lastIndexOf("/") + 1));
        }
        // 更新数据库
        user.setAvatar(avatar);
        userService.updateAvatar(user);
        return url;
    }
}
