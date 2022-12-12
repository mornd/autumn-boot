package com.mornd.system.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @author mornd
 * @dateTime 2021/11/8 - 11:53
 * 七牛云云存储服务器工具类
 */

@Component
@Slf4j
public class QiniuUtil {
    @Value("${qiniu.accessKey}")
    private String accessKey;
    @Value("${qiniu.secretKey}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("${qiniu.domain}")
    private String domain;

    @Resource
    private UploadManager uploadManager;

    private String uploadToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket);
    }

    /**
     * 根据文件路径
     * @param filePath
     * @param fileName
     * @return
     */
    public String upload(String filePath, String fileName) {
        String dateName = generateName(fileName);
        try {
            Response response = uploadManager.put(filePath, dateName, uploadToken());
            log.info("文件名称为{}上传成功！", dateName);
            return domain + dateName;
        } catch (QiniuException e) {
            Response r = e.response;
            log.error(r.toString());
            try {
                log.error("文件上传发生异常：{}", r.bodyString());
            } catch (QiniuException ex) {
                //ignore
            }
            return null;
        }
    }

    /**
     * 根据字节数组上传文件
     * @param bytes
     * @param fileName
     * @return
     */
    public String upload(byte[] bytes, String fileName) {
        //华南地区就选region2
        String dateName = generateName(fileName);
        try {
            Response response = uploadManager.put(bytes, generateName(fileName), uploadToken());
            log.info("文件名称为{}上传成功！！", dateName);
            return domain + dateName;
        } catch (QiniuException e) {
            Response r = e.response;
            log.error(r.toString());
            try {
                log.error("文件上传发生异常：{}", r.bodyString());
            } catch (QiniuException ex) {
                //ignore
            }
            return null;
        }
    }

    /**
     * 根据文件输入流上传文件
     * @param stream
     * @param fileName
     * @return
     */
    public String upload(InputStream stream, String fileName) {
        String dateName = generateName(fileName);
        try {
            Response response = uploadManager.put(stream, dateName, uploadToken(), null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info("上传的文件key：{}", putRet.key);
            log.info("上传的文件hash：{}", putRet.hash);
            log.info("上传的文件外链地址：" + domain + dateName);
            log.info("文件名称为{}上传成功！", dateName);
            return domain + dateName;
        } catch (QiniuException e) {
            Response r = e.response;
            log.error(r.toString());
            try {
                log.error("文件上传发生异常：{}", r.bodyString());
            } catch (QiniuException ex) {
                //ignore
            }
            return null;
        }
    }

    /**
     * 根据文件名删除文件
     * @param fileName
     */
    public void delete(String fileName) {
        Configuration config = new Configuration(Region.region2());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, config);
        try {
            bucketManager.delete(bucket, fileName);
            log.info("文件删除成功！");
        } catch (QiniuException e) {
            log.error("文件删除失败：{}", e.code());
            log.error(e.response.toString());
        }
    }

    /**
     * 根据当前时间生成唯一的文件名
     * @param fileName
     * @return
     */
    private String generateName(String fileName) {
        return AutumnUUID.fastUUID() + fileName;
    }
}
