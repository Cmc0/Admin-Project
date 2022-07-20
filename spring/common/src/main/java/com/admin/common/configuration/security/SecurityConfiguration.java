package com.admin.common.configuration.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.admin.common.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启 @PreAuthorize 权限注解
public class SecurityConfiguration {

    // 不需要登录就可以 下载文件的地址
    public final static String SYS_FILE_PUBLIC_DOWNLOAD_URL = "/sysFile/publicDownload";

    // 生产环境，Security 忽略的 url
    private static final List<String> PROD_IGNORING_LIST = CollUtil
        .newArrayList("/userRegister/**", "/userLogin/**", "/userForgotPassword/**", SYS_FILE_PUBLIC_DOWNLOAD_URL);

    // 其他环境，Security 忽略的 url
    private static final List<String> IGNORING_LIST = CollUtil
        .addAllIfNotContains(CollUtil.newArrayList("/swagger-resources/**", "/v3/api-docs", "/webjars/**", "/doc.html"),
            PROD_IGNORING_LIST);

    @Value("${spring.profiles.active:prod}")
    private String profile;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeRequests()
            .antMatchers(ArrayUtil.toArray("prod".equals(profile) ? PROD_IGNORING_LIST : IGNORING_LIST, String.class))
            .permitAll() // 可以匿名访问的请求
            .anyRequest().authenticated(); // 拦截所有请求

        httpSecurity.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).disable(); // 不需要session

        // 用户没有登录，但是访问需要权限的资源时，而报出的错误
        httpSecurity.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint());

        httpSecurity.csrf().disable(); // 关闭CSRF保护

        httpSecurity.logout().disable(); // 禁用 logout

        httpSecurity.formLogin().disable(); // 禁用 login

        return httpSecurity.build();
    }

}
