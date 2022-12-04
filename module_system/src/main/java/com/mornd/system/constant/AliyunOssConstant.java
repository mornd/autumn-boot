package com.mornd.system.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mornd
 * @dateTime 2022/11/10 - 22:54
 * 阿里云 oss 服务器配置常量
 */
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssConstant {
    private String endPoint;
    private String accessKeyID;
    private String accessKeySecret;
    private String  bucketName;

    /**
     * 地域节点
     */
    public static String END_POINT;
    /**
     * 访问 key
     */
    public static String ACCESS_KEY_ID;
    /**
     * 访问 secret
     */
    public static String ACCESS_KEY_SECRET;
    /**
     * 桶名称
     */
    public static String BUCKET_NAME;

    public void setEndPoint(String endPoint) {
        AliyunOssConstant.END_POINT = endPoint;
    }

    public void setAccessKeyID(String accessKeyID) {
        AliyunOssConstant.ACCESS_KEY_ID = accessKeyID;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AliyunOssConstant.ACCESS_KEY_SECRET = accessKeySecret;
    }

    public void setBucketName(String bucketName) {
        AliyunOssConstant.BUCKET_NAME = bucketName;
    }
}
