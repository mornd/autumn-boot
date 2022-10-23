package com.mornd.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mornd
 * @dateTime 2022/10/22 - 17:32
 */
@Data
@Component
@ConfigurationProperties(prefix = "autumn")
public class AutumnConfig {
    private String author;
    private String version;
    private String applicationName;
    private Integer uploadStorage;
    private String profile;
}
