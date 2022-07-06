package com.admin.common.model.constant;

/**
 * 通用的常量类
 */
public interface BaseConstant {

    String NEGATIVE_ONE_STR = "-1";

    // mq 相关 ↓
    String MQ_WEB_SOCKET_TOPIC = "cmc-admin-web-socket-topic"; // WebSocket的 topic
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

    // redis 相关 ↓ 【PRE_REDIS】开头和 【PRE_LOCK】开头，以及【_CACHE】结尾
    String PRE_REDISSON = "PRE_REDISSON:"; // 锁前缀，所有的分布式锁，都要加这个前缀

    String PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE = "PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE"; // 用户 id和私钥后缀 缓存

    // 登录失败次数过多，被锁定的账号，redis key前缀，锁 userId
    String PRE_REDIS_LOGIN_BLACKLIST = "PRE_REDIS_LOGIN_BLACKLIST:";
    String PRE_REDIS_LOGIN_ERROR_COUNT = "PRE_REDIS_LOGIN_ERROR_COUNT:"; // 登录失败，次数统计，redis key前缀

    String PRE_REDIS_XXL_JOB_COOKIE_CACHE = "PRE_REDIS_XXL_JOB_COOKIE_CACHE"; // xxl-job 登录 cookie缓存

    // 当前用户修改 相关 ↓
    // 当前用户：修改密码，发送的邮箱验证码，和分布式锁名前缀，锁邮箱
    String PRE_LOCK_SELF_UPDATE_PASSWORD_EMAIL_CODE = "PRE_LOCK_SELF_UPDATE_PASSWORD_EMAIL_CODE:";
    // 当前用户：修改邮箱，发送的邮箱验证码，和分布式锁名前缀，锁邮箱
    String PRE_LOCK_SELF_UPDATE_EMAIL_EMAIL_CODE = "PRE_LOCK_SELF_UPDATE_EMAIL_EMAIL_CODE:";
    // 当前用户：修改邮箱，发送的邮箱验证码兑换 key，和分布式锁名前缀，锁邮箱验证码
    String PRE_LOCK_SELF_UPDATE_EMAIL_EMAIL_CODE_CODE_TO_KEY = "PRE_LOCK_SELF_UPDATE_EMAIL_EMAIL_CODE_CODE_TO_KEY:";
    // 当前用户：注销，发送的邮箱验证码，和分布式锁名前缀，锁邮箱
    String PRE_LOCK_SELF_DELETE_EMAIL_CODE = "PRE_LOCK_SELF_DELETE_EMAIL_CODE:";
    // 忘记密码，发送，邮箱验证码，和分布式锁名前缀，锁邮箱
    String PRE_LOCK_SELF_FORGOT_PASSWORD_EMAIL_CODE = "PRE_LOCK_SELF_FORGOT_PASSWORD_EMAIL_CODE:";
    // 当前用户修改 相关 ↑

    // 用户获取权限相关 缓存 ↓
    String PRE_REDIS_ROLE_REF_USER_CACHE = "PRE_REDIS_ROLE_REF_USER_CACHE"; // 角色关联用户，缓存
    String PRE_REDIS_DEFAULT_ROLE_ID_CACHE = "PRE_REDIS_DEFAULT_ROLE_ID_CACHE"; // 默认角色 id，缓存
    String PRE_REDIS_ROLE_REF_MENU_CACHE = "PRE_REDIS_ROLE_REF_MENU_CACHE"; // 角色关联菜单，缓存
    String PRE_REDIS_MENU_ID_AND_AUTHS_LIST_CACHE = "PRE_REDIS_MENU_ID_AND_AUTHS_LIST_CACHE"; // 菜单id - 权限 的集合，缓存
    // 用户获取权限相关 缓存 ↑

    // 注册相关 ↓
    String PRE_LOCK_EMAIL_CODE = "PRE_LOCK_EMAIL_CODE:"; // 邮箱发送的验证码，和分布式锁名前缀，锁邮箱
    // 注册相关 ↑

    String PRE_REDIS_RSA_ENCRYPT = "PRE_REDIS_RSA_ENCRYPT:"; // 非对称加密存入 redis中 key前缀

    // WebSocket连接时，存储到 redis分布式锁名前缀，锁：【ip + port + 返回的随机码】
    String PRE_LOCK_WEB_SOCKET_REGISTER_CODE = "PRE_LOCK_WEB_SOCKET_REGISTER_CODE:";

    // jwt 相关 ↓
    String PRE_REDIS_JWT_HASH = "PRE_REDIS_JWT_HASH:"; // jwt在 redis中存储的 锁前缀，锁 jwtHash（jwt 生成的 hash）
    // jwt 相关 ↑

    String PRE_REDIS_PARAM_CACHE = "PRE_REDIS_PARAM_CACHE"; // 系统参数，redis缓存

    String PRE_REDIS_IP_BLACKLIST = "PRE_REDIS_IP_BLACKLIST:"; // ip黑名单，redis key前端
    String PRE_REDIS_IP_TOTAL_CHECK = "PRE_REDIS_IP_TOTAL_CHECK:"; // ip 请求速率，redis key前缀
    // redis 相关 ↑

}
