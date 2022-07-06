package com.admin.common.util;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.SysMenuDO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.enums.SysRequestCategoryEnum;
import com.admin.common.model.vo.ApiResultVO;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class MyJwtUtil {

    // 系统里的 jwt密钥
    private static final String JWT_SECRET_SYS =
        "4283dde8cb54c0c68082ada1b1d9ce048195cd3090e07dfad3e1871b462a8b75fee46467b96f33dea6511869f1ea4867aed76243dfe7e1efb89338d3da6570d1";

    public static JsonRedisTemplate<String> jsonRedisTemplate;

    @Resource
    private void setJsonRedisTemplate(JsonRedisTemplate<String> value) {
        jsonRedisTemplate = value;
    }

    /**
     * 统一生成 jwt
     */
    public static String generateJwt(Long userId, boolean rememberMe, String jwtSecretSuf,
        SysRequestCategoryEnum sysRequestCategoryEnum) {

        if (userId == null) {
            userId = (Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userId == null) {
                return null;
            }
        }

        if (BaseConstant.ADMIN_ID.equals(userId) && !BaseConfiguration.adminProperties.isAdminEnable()) {
            return null;
        }

        if (StrUtil.isBlank(jwtSecretSuf)) {
            jwtSecretSuf = getUserJwtSecretSufByUserId(userId);
        }

        if (!BaseConstant.ADMIN_ID.equals(userId) && StrUtil.isBlank(jwtSecretSuf)) {
            return null;
        }

        return sign(userId, jwtSecretSuf, rememberMe, sysRequestCategoryEnum);
    }

    /**
     * 生成 jwt
     */
    private static String sign(Long userId, String jwtSecretSuf, boolean rememberMe,
        SysRequestCategoryEnum sysRequestCategoryEnum) {

        JSONObject payloadMap = JSONUtil.createObj().set("userId", userId);

        // 设置过期时间
        long expireTime;
        if (rememberMe) {
            expireTime = BaseConstant.DAY_7_EXPIRE_TIME; // 7天过期
        } else {
            expireTime = BaseConstant.DAY_1_EXPIRE_TIME; // 1天过期
        }

        String fullJwt = BaseConstant.JWT_PREFIX + JWT.create() //
            .setExpiresAt(new Date(System.currentTimeMillis() + expireTime)) // 设置过期时间
            .addPayloads(payloadMap) // 增加JWT载荷信息
            .setKey(getJwtSecret(jwtSecretSuf).getBytes()) // 设置密钥
            .sign();

        // 存储到 redis中
        expireTime = expireTime - BaseConstant.SECOND_30_EXPIRE_TIME;
        String redisJwtHash = generateRedisJwtHash(fullJwt, userId, sysRequestCategoryEnum);
        jsonRedisTemplate.opsForValue().set(redisJwtHash, "jwtHash", expireTime, TimeUnit.MILLISECONDS);

        return fullJwt;
    }

    /**
     * 获取 jwt密钥：配置的私钥前缀 + JWT_SECRET_SYS + 用户的私钥后缀
     */
    public static String getJwtSecret(String jwtSecretSuf) {

        StrBuilder strBuilder = StrBuilder.create(BaseConfiguration.adminProperties.getJwtSecretPre());
        strBuilder.append(JWT_SECRET_SYS).append(jwtSecretSuf);

        return strBuilder.toString();
    }

    /**
     * 生成 redis中，jwt存储使用的 key（jwtHash），目的：不直接暴露明文的 jwt
     */
    public static String generateRedisJwtHash(String jwt, Long userId, SysRequestCategoryEnum sysRequestCategoryEnum) {

        StrBuilder strBuilder = StrBuilder.create(BaseConstant.PRE_REDIS_JWT_HASH);
        strBuilder.append(userId).append(":").append(sysRequestCategoryEnum.getCode()).append(":")
            .append(DigestUtil.sha512Hex(jwt));

        return strBuilder.toString();
    }

    /**
     * 移除：全部 jwtHash
     */
    public static void removeAllJwtHash() {

        Set<String> jwtHashSet = RedisUtil.scan(BaseConstant.PRE_REDIS_JWT_HASH);

        if (jwtHashSet.size() != 0) {
            jsonRedisTemplate.delete(jwtHashSet); // 移除：全部 jwtHash
        }

    }

    /**
     * 获取用户 jwt私钥后缀，通过 userId
     */
    public static String getUserJwtSecretSufByUserId(Long userId) {

        if (userId == null || BaseConstant.ADMIN_ID.equals(userId)) {
            return null;
        }

        String userIdStr = userId.toString();

        BoundHashOperations<String, String, String> ops =
            jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE);

        String jwtSecretSuf = ops.get(userIdStr);

        if (jwtSecretSuf == null) {
            SysUserDO sysUserDO = UserUtil.getUserJwtSecretSufByUserId(userId);
            if (sysUserDO == null || StrUtil.isBlank(sysUserDO.getJwtSecretSuf())) {
                ops.put(userIdStr, "");
                return null;
            } else {
                jwtSecretSuf = sysUserDO.getJwtSecretSuf();
                ops.put(userIdStr, jwtSecretSuf);
            }
        }

        return jwtSecretSuf;
    }

    /**
     * 更新：redis中，userId和 jwtSecretSuf对应关系
     */
    public static void updateUserIdJwtSecretSufForRedis(Long userId, String jwtSecretSuf) {
        jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE)
            .put(userId.toString(), jwtSecretSuf);
    }

    /**
     * 更新：redis中，userId和 jwtSecretSuf对应关系，可批量
     */
    public static void updateUserIdJwtSecretSufForRedis(Map<String, String> map) {
        jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE).putAll(map);
    }

    /**
     * 删除：redis中，userId和 jwtSecretSuf对应关系，可批量
     */
    public static void deleteUserIdJwtSecretSufForRedis(Set<String> userIdSet) {
        jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_USER_ID_JWT_SECRET_SUF_CACHE)
            .delete(ArrayUtil.toArray(userIdSet, String.class));
    }

    /**
     * 通过 userId获取到权限的 set
     */
    public static List<SimpleGrantedAuthority> getAuthSetByUserId(Long userId) {

        if (userId == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST); // 直接抛出异常
            return null;
        }

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            return null; // admin自带所有权限
        }

        List<SimpleGrantedAuthority> resultList = new ArrayList<>();

        List<SysMenuDO> menuList = UserUtil.getMenuListByUserId(userId, 2); // 通过用户 id，获取 菜单集合

        if (menuList.size() == 0) {
            return resultList;
        }

        Set<String> authsSet = menuList.stream().map(SysMenuDO::getAuths).collect(Collectors.toSet());

        // 组装权限，并去重
        Set<String> hashSet = new HashSet<>();
        for (String item : authsSet) {
            if (StrUtil.isBlank(item)) {
                continue;
            }
            String[] split = item.split(",");
            for (String auth : split) {
                if (StrUtil.isNotBlank(auth)) {
                    hashSet.add(auth);
                }
            }
        }

        resultList = hashSet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return resultList;
    }

}
