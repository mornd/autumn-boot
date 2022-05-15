package com.mornd.system.config;

import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mornd
 * @dateTime 2022/1/25 - 14:32
 */

@Configuration
public class QiniuConfig {
    
    @Bean
    public UploadManager getUploadManager() {
        //华南地区就选region2
        com.qiniu.storage.Configuration config = new com.qiniu.storage.Configuration(Region.region2());
        return new UploadManager(config);
    }
}
