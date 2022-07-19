package com.admin.common.configuration.mvc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Bean
    @Primary
    public DispatcherServlet dispatcherServlet() {
        return new MyDispatcherServlet();
    }

}

