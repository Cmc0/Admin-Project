package com.cmc.common.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.cmc")
@MapperScan(basePackages = "com.cmc.**.mapper")
public class BaseConfiguration {

    public static String applicationName;

    public BaseConfiguration(ApplicationContext applicationContext) {
        applicationName = applicationContext.getApplicationName();
    }

}
