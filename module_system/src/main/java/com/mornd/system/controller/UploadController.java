package com.mornd.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.UserService;
import com.mornd.system.utils.QiniuUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 文件上传控制器
 * @author mornd
 * @dateTime 2022/1/24 - 23:18
 */

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Resource
    private QiniuUtil qiniuUtil;
    @Resource
    private UserService userService;
    
    /**
     * 头像上传
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/avatar")
    public JsonResult upload(@RequestBody MultipartFile file, HttpServletRequest request) throws IOException {
        if(file == null) return JsonResult.failure("文件为空");
        String id = request.getParameter("id");
        if(StringUtils.isBlank(id)) return JsonResult.failure("用户id不能为空");
        //查询用户之前的头像
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        qw.eq(SysUser::getId, id);
        qw.last("LIMIT 1");
        SysUser user = userService.getOne(qw);
        
        String url = qiniuUtil.upload(file.getInputStream(), file.getOriginalFilename());
        if(url == null) {
            return JsonResult.failure("上传失败，请重试");
        }
        //删除之前的头像
        String avatar = user.getAvatar();
        if(StrUtil.isNotBlank(avatar)) {
            qiniuUtil.delete(avatar.substring(avatar.lastIndexOf("/") + 1));
        }
        return JsonResult.success("上传成功", url);
    }
}
