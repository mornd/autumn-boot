package com.mornd.system.controller.base;

import com.mornd.system.entity.po.SysUser;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: mornd
 * @dateTime: 2023/3/26 - 13:43
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected JsonResult success() {
        return JsonResult.success();
    }

    protected JsonResult failure() {
        return JsonResult.failure();
    }

    /**
     * 获取当前登录用户对象
     * @return
     */
    protected SysUser getLoginUser() {
        return SecurityUtil.getLoginUser();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return String.format("redirect:{}", url);
    }

    /**
     * 检测上传的文件是否为空
     * @param file
     */
    protected void checkFile(MultipartFile file) {
        if(file == null || file.isEmpty()) {
            throw new BadRequestException("上传失败，文件或者文件类型不能为空");
        }
    }
}
