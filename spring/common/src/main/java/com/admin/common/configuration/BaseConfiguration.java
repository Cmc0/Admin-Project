package com.admin.common.configuration;

import cn.hutool.system.SystemUtil;
import com.admin.common.properties.AdminProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@ComponentScan(basePackages = "com.admin")
@MapperScan(basePackages = "com.admin.**.mapper")
public class BaseConfiguration {

    public static String applicationName; // 服务名
    public static final String HOST_NAME = SystemUtil.getHostInfo().getName(); // 当前主机的名称
    public static Integer port; // 启动的端口
    public static boolean prodFlag = true; // 是否是正式环境
    public static AdminProperties adminProperties; // 本系统相关配置

    @Value("${spring.application.name:admin-project}")
    private void setApplicationName(String value) {
        applicationName = value;
    }

    @Resource
    private void setAdminProperties(AdminProperties value) {
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
