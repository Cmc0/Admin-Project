package com.admin.common.model.constant;

/**
 * 通用的常量类
 */
public interface BaseConstant {

    String NEGATIVE_ONE_STR = "-1";

    // mq 相关 ↓
    String MQ_WEB_SOCKET_TOPIC = "admin-project-web-socket-topic"; // webSocket的 topic
    // mq 相关 ↑

    // 过期时间相关 ↓
    long DAY_1_EXPIRE_TIME = 60 * 60 * 1000 * 24; // 1天过期
    long DAY_7_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 7L; // 7天过期
    long DAY_15_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 15L; // 15天过期
    long DAY_30_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 30L; // 30天过期
    long YEAR_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 365L; // 一年过期
    int MINUTE_30_EXPIRE_TIME = 30 * 60 * 1000; // 30分钟过期
    int MINUTE_10_EXPIRE_TIME = 10 * 60 * 1000; // 10分钟过期
    int MINUTE_1_EXPIRE_TIME = 60 * 1000; // 1分钟过期
    int SECOND_1_EXPIRE_TIME = 1000; // 1秒钟过期
    int SECOND_2_EXPIRE_TIME = 2000; // 2秒钟过期
    int SECOND_6_EXPIRE_TIME = 6 * 1000; // 6秒钟过期
    int SECOND_10_EXPIRE_TIME = 10 * 1000; // 10秒钟过期
    int SECOND_30_EXPIRE_TIME = 30 * 1000; // 30秒钟过期
    // 过期时间相关 ↑

    // jwt 相关 ↓
    String JWT_HEADER_KEY = "Authorization";
    String JWT_PREFIX = "Bearer ";
    // jwt 相关 ↑

    // properties 相关 ↓
    String ADMIN = "admin"; // 本系统相关配置前缀
    // properties 相关 ↑

    // id 相关 ↓
    Long ADMIN_ID = 0L; // 管理员 id
    Long SYS_ID = -1L; // 系统 id
    // id 相关 ↑

    // 参数配置相关 ↓
    Long RSA_PRIVATE_KEY_ID = 1L; // 非对称加密，密钥 主键id
    Long IP_REQUESTS_PER_SECOND_ID = 2L; // ip请求速率 主键id
    // 参数配置相关 ↑

    // request 相关 ↓
    String REQUEST_HEADER_CATEGORY = "category"; // 类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
    String USER_LOGIN_PATH = "/userLogin"; // 用户登录路径：前缀
    // request 相关 ↑

}
