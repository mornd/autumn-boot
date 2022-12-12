package com.mornd.system.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.mornd.system.constant.AliyunOssConstant;
import com.mornd.system.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author mornd
 * @dateTime 2022/11/10 - 23:37
 */
@Slf4j
public class AliyunOssUtil {
    /**
     * 存储的文件夹名称
     */
    private static final String OBJECT_DIR = "avatar/";

    /**
     * 基于输入流进行文件上传
     * @param inputStream 输入流
     * @return 访问地址
     */
    public static String upload(InputStream inputStream, String fileName) {
        //  截取后缀
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        // 重写文件名称
        fileName = OBJECT_DIR + AutumnUUID.fastUUID() + suffix;
        String endpoint = AliyunOssConstant.END_POINT;
        String accessKeyId = AliyunOssConstant.ACCESS_KEY_ID;
        String accessKeySecret = AliyunOssConstant.ACCESS_KEY_SECRET;
        String bucketName = AliyunOssConstant.BUCKET_NAME;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, fileName, inputStream);
            // https://autumn-01.oss-cn-guangzhou.aliyuncs.com/avatar/tom.jpg
            return "https://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (OSSException oe) {
            log.info("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.info("Error Message:" + oe.getErrorMessage());
            log.info("Error Code:" + oe.getErrorCode());
            log.info("Request ID:" + oe.getRequestId());
            log.info("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            log.info("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.info("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return "";
    }

    /**
     * 对象删除
     * @param filePath
     */
    public static void delete(String filePath) {
        try {
            filePath = filePath.substring(filePath.lastIndexOf(OBJECT_DIR));
        } catch (Exception e) {
            return;
        }
        String endpoint = AliyunOssConstant.END_POINT;
        String accessKeyId = AliyunOssConstant.ACCESS_KEY_ID;
        String accessKeySecret = AliyunOssConstant.ACCESS_KEY_SECRET;
        String bucketName = AliyunOssConstant.BUCKET_NAME;
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.deleteObject(bucketName, filePath);
        } catch (Exception e) {
            log.error("aliyun oss 对象删除出现异常");
            throw new BadRequestException("aliyun oss 对象删除出现异常");
        } finally {
            if(ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
