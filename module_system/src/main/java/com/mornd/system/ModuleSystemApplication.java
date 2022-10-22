package com.mornd.system;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;

/**
 * @Author mornd
 */
@Slf4j
@SpringBootApplication
@EnableTransactionManagement
public class ModuleSystemApplication implements ApplicationListener<WebServerInitializedEvent> {
    private static WebServerInitializedEvent event;
    /**
     * 主启动方法
     * @param args
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext
                = SpringApplication.run(ModuleSystemApplication.class, args);

        printStartedMessage();
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        event = webServerInitializedEvent;
    }

    /**
     * 打印启动成功的信息
     */
    @SneakyThrows
    private static void printStartedMessage() {
        WebServer server = event.getWebServer();
        WebServerApplicationContext context = event.getApplicationContext();
        Environment env = context.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        int port = server.getPort();
        String contextPath = env.getProperty("server.servlet.context-path");
        if (contextPath == null) {
            contextPath = "";
        }
        log.info("\n---------------------------------------------------------\n" +
                "\tApplication is running! Access address:\n" +
                 "\tLocal:\t\thttp://localhost:{}{}" +
                "\n\tExternal:\thttp://{}:{}{}" +
                "\n---------------------------------------------------------\n", port, contextPath, ip, port, contextPath);
        log.info("autumn-boot application started successfully!!!");
    }
}
