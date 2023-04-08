package com.mornd.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author mornd
 * @dateTime 2022/10/22 - 17:32
 */
@Data
@ConfigurationProperties(prefix = "autumn")
public class AutumnConfig {
    @Resource
    private ServletWebServerApplicationContext applicationContext;

    private String author;
    private String version;
    private String applicationName;
    private Integer uploadStorage;
    private String profile;

    /**
     * 用户注册成功时是否发送通知邮件
     */
    private boolean userMailNotification;

    /**
     * 系统前端地址
     */
    private String uiBaseUrl;
    /**
     *  oa 移动端地址
     */
    private String oaUiBaseUrl;

    private static String serverBaseUrl;

    /**
     * 获取服务器的ip，端口和前缀
     * http://2.0.0.1:9001
     * @return
     */
    public String getServerBaseUrl() {
        if(serverBaseUrl == null) {
            String ip = null;
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                ip = "localhost";
            }

            WebServer webServer = applicationContext.getWebServer();
            int port = webServer.getPort();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            String contextPath = environment.getProperty("server.servlet.context-path");
            if (contextPath == null) {
                contextPath = "";
            }
            serverBaseUrl = String.format("http://%s:%s%s", ip, port, contextPath);
        }
        return serverBaseUrl;
    }
}
