package com.cmc.projectutil.configuration;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@Slf4j
@EnableSwagger2
public class Knife4jConfiguration {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30).apiInfo(new ApiInfoBuilder().title("projectutil 接口文档").build())
            .select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) // 方法需要有ApiOperation注解才能生成接口文档
            .paths(PathSelectors.any()).build();
    }

}

