package com.cmc.projectutil.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.cmc")
public class BaseConfiguration {

    public static String applicationName;

    public BaseConfiguration(ApplicationContext applicationContext) {
        applicationName = applicationContext.getApplicationName();
    }

}
