package com.admin.common.model.constant;

/**
 * Redis相关的常量类
 */
public interface BaseRedisConstant {

    // redis 相关 ↓ 【PRE_REDIS】开头和 【PRE_LOCK】开头，以及【_CACHE】结尾

    String PRE_REDISSON = "PRE_REDISSON:"; // 锁前缀，所有的分布式锁，都要加这个前缀

    String PRE_LOCK_BULLETIN_ID = ""; // 公告修改，发布，取消时的 id锁前缀

    String PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE = "PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE";
    // 用户 id和私钥后缀 缓存，以及分布式锁，锁用户 主键 id

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
