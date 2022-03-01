package com.mornd.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ModuleSystemApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ModuleSystemApplication.class, args);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        int count = context.getBeanDefinitionCount();
        System.out.println("Application Started Successfully!!!");
    }

}
