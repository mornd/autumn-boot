package com.mornd.system.config;

import com.mornd.system.entity.po.SysUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mornd
 * @dateTime 2021/9/13 - 8:55
 * 浏览器输入swagger文档地址：http://localhost:1001/dataView/doc.html
 */

@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    private final String basePackage = "com.mornd.system.controller";
    private final String title = "autumn接口文档";
    private final String version = "1.1";
    private final String description = "autumn接口文档";
    private final String name = "mornd";
    private final String url = "http://localhost:1001/dataView/doc.html";
    private final String email = "1152229579@qq.com";
    
    @Bean
    public Docket createApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title(title)
                .version(version)
                .description(description)
                .contact(new Contact(name, url, email))
                .build();
    }

    private List<ApiKey> securitySchemes(){
        List<ApiKey> list = new ArrayList<>();
        ApiKey apiKey = new ApiKey(tokenHeader, tokenHeader, "Header");
        list.add(apiKey);
        return list;
    }

    private List<SecurityContext> securityContexts(){
        List<SecurityContext> list = new ArrayList<>();
        list.add(getSecurityContext());
        return list;
    }

    private SecurityContext getSecurityContext(){
        return SecurityContext.builder().securityReferences(securityReferences())
                .forPaths(PathSelectors.regex("/hello/.*"))
                .build();
    }

    private List<SecurityReference> securityReferences(){
        List<SecurityReference> list = new ArrayList<>();
        //授权范围：全局
        AuthorizationScope scope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] scopes = new AuthorizationScope[1];
        scopes[0] = scope;
        list.add(new SecurityReference(tokenHeader,scopes));
        return list;
    }
}
