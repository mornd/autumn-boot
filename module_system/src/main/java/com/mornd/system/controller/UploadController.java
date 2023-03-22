package com.mornd.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.service.UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 文件存储控制器
 * @author mornd
 * @dateTime 2022/1/24 - 23:18
 */

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Resource
    private UploadService uploadService;
    /**
     * 头像上传
     * @param file
     * @return
     * @throws IOException
     */
    @LogStar(value = "上传头像", BusinessType = LogType.UPLOAD)
    @PostMapping("/avatar")
    public JsonResult uploadAvatar(@RequestBody MultipartFile file, HttpServletRequest request) throws Exception {
        if(file == null || file.getSize() <= 0) return JsonResult.failure("文件为空");
        String id = request.getParameter("id");
        if(StringUtils.isBlank(id)) return JsonResult.failure("用户id不能为空");
        String url = uploadService.uploadAvatar(id, file);
        return JsonResult.success("上传成功", url);
    }
}
