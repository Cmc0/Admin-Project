package com.admin.common.configuration;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Knife4jConfiguration {

    @Value("${spring.profiles.active:prod}")
    private String profile;

    @Bean
    public Docket createRestApi() {

        boolean enable = !"prod".equals(profile); // 不是生产环境，才启动swagger

        return new Docket(DocumentationType.OAS_30).enable(enable).select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) // 方法需要有ApiOperation注解才能生成接口文档
            .paths(PathSelectors.any()).build();
    }

}

