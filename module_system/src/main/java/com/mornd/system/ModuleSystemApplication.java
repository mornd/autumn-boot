package com.mornd.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ModuleSystemApplication {

    /**
     * 主启动方法
     * @param args
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext 
                = SpringApplication.run(ModuleSystemApplication.class, args);
        System.out.println("Autumn-Boot Application Started Successfully!!!");
    }
}
