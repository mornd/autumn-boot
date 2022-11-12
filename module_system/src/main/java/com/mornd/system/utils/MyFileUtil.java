package com.mornd.system.utils;

import cn.hutool.core.io.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author mornd
 * @dateTime 2022/10/23 - 13:49
 */
public class MyFileUtil extends FileUtil {
    /**
     * 创建文件
     * @param file
     * @return 文件是否创建成功
     */
    public static boolean mkdirs(File file) {
        return !file.exists() && file.mkdirs();
    }

    public static boolean mkdirs(String path) {
        return mkdirs(new File(path));
    }

    /**
     * 获取上传文件的后缀
     * @param file
     * @return
     */
    public static String getFileSuffix(MultipartFile file) {
        //获取上传的文件名
        String fileName = file.getOriginalFilename();
        //获取文件后缀
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
