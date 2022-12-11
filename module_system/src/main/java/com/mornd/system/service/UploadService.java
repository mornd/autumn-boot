package com.mornd.system.service;

import org.springframework.web.multipart.MultipartFile;


/**
 * @author mornd
 * @dateTime 2022/10/22 - 20:16
 */
public interface UploadService {

    String uploadAvatar(String id, MultipartFile file) throws Exception;

    void deleteAvatar(String path);
}
