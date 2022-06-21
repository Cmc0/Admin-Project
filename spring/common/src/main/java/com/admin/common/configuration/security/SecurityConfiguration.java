package com.admin.common.configuration.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // 不需要登录就可以 下载文件的地址
    public final static String FILE_PUBLIC_DOWNLOAD_URL = "/file/publicDownload";

    // 生产环境，不需要 Security 处理的 url
    private static final List<String> PROD_IGNORING_LIST =
        Arrays.asList("/userRegister/**", "/userLogin/**", FILE_PUBLIC_DOWNLOAD_URL);

    // 其他环境，不需要 Security 处理的 url
    private static final List<String> IGNORING_LIST = CollUtil
        .addAllIfNotContains(CollUtil.newArrayList("/swagger-resources/**", "/v3/api-docs", "/webjars/**", "/doc.html"),
            PROD_IGNORING_LIST);

    @Override
    public void configure(WebSecurity web) {

        web.ignoring()
            .antMatchers(ArrayUtil.toArray(BaseConfiguration.prodFlag ? PROD_IGNORING_LIST : IGNORING_LIST, String.class));

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().anyRequest().authenticated(); // 拦截所有请求

        http.addFilter(new JwtAuthorizationFilter(authenticationManager()));

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 不需要session

        // 用户没有登录，但是访问需要权限的资源时，而报出的错误
        http.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint());

        http.csrf().disable(); // 关闭CSRF保护
    }

}
