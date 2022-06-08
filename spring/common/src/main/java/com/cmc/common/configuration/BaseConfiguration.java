package com.cmc.common.configuration;

import cn.hutool.system.SystemUtil;
import com.cmc.common.properties.AdminProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@ComponentScan(basePackages = "com.cmc")
@MapperScan(basePackages = "com.cmc.**.mapper")
public class BaseConfiguration {

    public static String applicationName; // 服务名
    public static String address = SystemUtil.getHostInfo().getAddress(); // 当前主机的地址
    public static Integer port; // 启动的端口
    public static boolean prodFlag; // 是否是正式环境
    public static AdminProperties adminProperties; // 本系统相关配置

    @Value("${spring.application.name:admin}")
    private void setApplicationName(String value) {
        applicationName = value;
    }

    @Resource
    private void setCmc6AdminProperties(AdminProperties value) {
        adminProperties = value;
    }

    @Value("${server.port:8080}")
    private void setPort(Integer value) {
        port = value;
    }

    @Value("${spring.profiles.active:prod}")
    private void setProdFlag(String value) {
        prodFlag = "prod".equals(value);
    }

}
