package com.admin.common.configuration.mvc;

import com.admin.common.interceptor.MyHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Resource
    MyHandlerInterceptor myHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myHandlerInterceptor).addPathPatterns("/**"); // myHandlerInterceptor 里面自己过滤不需要的 url
    }

    @Bean
    @Primary
    public DispatcherServlet dispatcherServlet() {
        return new MyDispatcherServlet();
    }

}

