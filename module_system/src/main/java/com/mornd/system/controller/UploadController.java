package com.mornd.system.controller;

import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.utils.QiniuUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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

    /**
     * 头像上传
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/image")
    public JsonResult upload(@RequestBody MultipartFile file) throws IOException {
        if(file == null) {
            return JsonResult.failure("文件为空");
        }
        String url = qiniuUtil.upload(file.getInputStream(), file.getOriginalFilename());
        return JsonResult.success("上传成功", url);
    }
}
