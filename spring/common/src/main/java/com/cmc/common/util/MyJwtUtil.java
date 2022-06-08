package com.cmc.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.cmc.common.configuration.BaseConfiguration;
import com.cmc.common.exception.BaseBizCodeEnum;
import com.cmc.common.mapper.*;
import com.cmc.common.model.constant.BaseConstant;
import com.cmc.common.model.entity.BaseMenuDO;
import com.cmc.common.model.entity.BaseUserSecurityDO;
import com.cmc.common.model.enums.RequestCategoryEnum;
import com.cmc.common.model.vo.ApiResultVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Data
@Slf4j
public class MyJwtUtil {

    public static BaseRoleRefUserMapper baseRoleUserMapper;

    @Resource
    private void setBaseRoleUserMapper(BaseRoleRefUserMapper value) {
        baseRoleUserMapper = value;
    }

    public static BaseRoleRefMenuMapper baseRoleMenuMapper;

    @Resource
    private void setBaseRoleMenuMapper(BaseRoleRefMenuMapper value) {
        baseRoleMenuMapper = value;
    }

    public static BaseMenuMapper baseMenuMapper;

    @Resource
    private void setBaseMenuMapper(BaseMenuMapper value) {
        baseMenuMapper = value;
    }

    public static BaseUserMapper baseUserMapper;

    @Resource
    private void setBaseUserMapper(BaseUserMapper value) {
        baseUserMapper = value;
    }

    public static BaseUserSecurityMapper baseUserSecurityMapper;

    @Resource
    private void setBaseUserSecurityMapper(BaseUserSecurityMapper value) {
        baseUserSecurityMapper = value;
    }

    // 系统里的 jwt密钥
    private static final String JWT_SECRET_SYS =
        "4283dde8cb54c0c68082ada1b1d9ce048195cd3090e07dfad3e1871b462a8b75fee46467b96f33dea6511869f1ea4867aed76243dfe7e1efb89338d3da6570d1";

    public static RedisTemplate<String, String> redisTemplate;

    @Resource
    private void setRedisTemplate(RedisTemplate<String, String> value) {
        redisTemplate = value;
    }

    /**
     * 统一生成 jwt
     */
    public static String generateJwt(Long userId, boolean rememberMe, String jwtSecretSuf) {
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

        if (StrUtil.isBlank(jwtSecretSuf) && !BaseConstant.ADMIN_ID.equals(userId)) {
            return null;
        }

        return sign(userId, jwtSecretSuf, rememberMe);
    }

    /**
     * 生成 jwt
     */
    private static String sign(Long userId, String jwtSecretSuf, boolean rememberMe) {

        JSONObject payloadMap = JSONUtil.createObj().set("userId", userId);

        // 设置过期时间
        long expireTime;
        if (rememberMe) {
            expireTime = BaseConstant.DAY_7_EXPIRE_TIME; // 7天过期
        } else {
            expireTime = BaseConstant.DAY_1_EXPIRE_TIME; // 1天过期
        }

        String jwt = BaseConstant.JWT_PREFIX + JWT.create() //
            .setExpiresAt(new Date(System.currentTimeMillis() + expireTime)) // 设置过期时间
            .addPayloads(payloadMap) // 增加JWT载荷信息
            .setKey(getJwtSecret(jwtSecretSuf).getBytes()) // 设置密钥
            .sign();

        // 存储到 redis中
        expireTime = expireTime - BaseConstant.SECOND_30_EXPIRE_TIME;
        String redisJwtHash = generateRedisJwtHash(jwt);
        redisTemplate.opsForValue().set(redisJwtHash, "jwt", expireTime, TimeUnit.MILLISECONDS);

        String jwtUserKey = generateRedisJwtUserKey(userId);
        redisTemplate.boundSetOps(jwtUserKey).add(redisJwtHash); // 添加元素
        redisTemplate.expire(jwtUserKey, BaseConstant.DAY_7_EXPIRE_TIME - BaseConstant.SECOND_30_EXPIRE_TIME,
            TimeUnit.MILLISECONDS); // 备注：这里设置为 7天 - 30秒过期，因为最久的 jwt过期时间也是这个

        return jwt;
    }

    /**
     * 获取 jwt密钥：配置的私钥前缀 + JWT_SECRET_SYS + 用户的私钥后缀
     */
    public static String getJwtSecret(String jwtSecretSuf) {
        StrBuilder strBuilder = new StrBuilder(BaseConfiguration.adminProperties.getJwtSecretPre());
        strBuilder.append(JWT_SECRET_SYS).append(jwtSecretSuf);
        return strBuilder.toString();
    }

    /**
     * 生成 redis中，jwt存储使用的 key
     * 格式：userId + 类别: jwtHashSet
     */
    public static String generateRedisJwtUserKey(Long userId) {
        return generateRedisJwtUserKeyPre(userId) + RequestUtil.getRequestCategoryEnum().getCode();
    }

    /**
     * 生成 redis中，jwt存储使用的 key前面一部分值
     */
    public static String generateRedisJwtUserKeyPre(Long userId) {
        return BaseConstant.PRE_REDIS_JWT_USER + userId + ":";
    }

    /**
     * 生成 redis中，jwt存储使用的 key（jwtHash）
     */
    public static String generateRedisJwtHash(String jwt) {
        return BaseConstant.PRE_REDIS_JWT + DigestUtil.sha256Hex(jwt);
    }

    /**
     * 清理过期了，但是还是存在于 jwtUser set里面的 jwtHash
     */
    public static void removeJwtHashForJwtUser(Long userId) {

        String jwtUserKeyPre = generateRedisJwtUserKeyPre(userId);

        for (RequestCategoryEnum item : RequestCategoryEnum.values()) {
            String jwtUserKey = jwtUserKeyIsExist(jwtUserKeyPre, item);
            if (jwtUserKey == null) {
                continue;
            }
            // 获取 jwtUser set里面所有的 jwtHash
            BoundSetOperations<String, String> setOps = redisTemplate.boundSetOps(jwtUserKey);
            Set<String> jwtHashSet = setOps.members();
            if (CollUtil.isEmpty(jwtHashSet)) {
                continue;
            }
            Set<String> removeSet = new HashSet<>(); // 需要移除：过期了，但是还是存在于 jwtUser set里面的 jwtHash
            for (String subItem : jwtHashSet) {
                Boolean aBoolean = redisTemplate.hasKey(subItem);
                if (aBoolean == null || !aBoolean) {
                    // 如果这个 jwtHash不存在了，则移除 jwtUser set里面的 jwtHash
                    removeSet.add(subItem);
                }
            }
            if (removeSet.size() != 0) {
                setOps.remove(removeSet.toArray()); // 执行批量移除
            }
        }

    }

    /**
     * 移除全部的 jwt
     */
    public static void removeAllJwtHash() {

        Set<String> jwtSet = RedisUtil.scan(BaseConstant.PRE_REDIS_JWT);
        Set<String> jwtUserSet = RedisUtil.scan(BaseConstant.PRE_REDIS_JWT_USER);

        jwtSet.addAll(jwtUserSet);

        if (jwtSet.size() != 0) {
            redisTemplate.delete(jwtSet); // 移除 全部 jwt
        }

    }

    /**
     * 通过 requestCategoryEnum或者 jwtHash清理：jwtUser set里面的 jwtHash，以及 jwtHash
     * userId 不能为 null，其他全部为 null时，则表示：移除 userId下的所有 jwt
     * requestCategoryEnum：移除指定的 requestCategoryEnum，不传则移除全部
     * jwtHash：如果传了这个值，那么 requestCategoryEnum，anyOneFlag不会生效
     * anyOneFlag：是否随机剩下一个，为 null，则不进行操作，为 true 则 全部只剩一个 为 false 则 各个类别都剩一个
     */
    public static void removeJwtHashByRequestCategoryOrJwtHash(Long userId, RequestCategoryEnum requestCategoryEnum,
        String jwtHash, Boolean anyOneFlag) {

        boolean jwtHashFlag = StrUtil.isNotBlank(jwtHash);
        if (jwtHashFlag) {
            redisTemplate.delete(jwtHash); // 移除 jwtHash
            requestCategoryEnum = null;
        }
        if (anyOneFlag != null) {
            requestCategoryEnum = null;
        }

        String jwtUserKeyPre = generateRedisJwtUserKeyPre(userId);

        Set<String> removeKeySet = new HashSet<>(); // redis需要移除的 keySet

        boolean allAnyOneFlag = false; // anyOneFlag == true 时使用：用于判断是否留了一个

        for (RequestCategoryEnum item : RequestCategoryEnum.values()) {

            if (requestCategoryEnum == null) { // 如果等于 null，则移除全部
            } else if (!item.equals(requestCategoryEnum)) { // 如果不为 null，则只移除指定的 requestCategoryEnum
                continue;
            }

            String jwtUserKey = jwtUserKeyIsExist(jwtUserKeyPre, item);
            if (jwtUserKey == null) {
                continue;
            }
            // 获取 jwtUser set里面所有的 jwtHash
            BoundSetOperations<String, String> setOps = redisTemplate.boundSetOps(jwtUserKey);

            if (jwtHashFlag) {
                Long remove = setOps.remove(jwtHash);
                if (remove != null && remove != 0) {
                    return; // 如果移除成功，则直接返回
                }
                continue; // 继续下一次循环，即：下面的代码都不会执行
            }

            Set<String> jwtHashSet = setOps.members();
            if (CollUtil.isEmpty(jwtHashSet)) {
                continue;
            }

            if (anyOneFlag != null && !anyOneFlag) {
                // 各个类别都剩一个
                jwtHashSetAnyOne(setOps, jwtHashSet, removeKeySet); // jwtHashSet，随机剩下一个
            } else {
                if (anyOneFlag != null) {
                    // 全部只剩一个
                    if (!allAnyOneFlag) {
                        allAnyOneFlag = true; // 赋值为 true，表示已经 随机剩了一个
                        jwtHashSetAnyOne(setOps, jwtHashSet, null); // jwtHashSet，随机剩下一个
                        continue; // 继续下一次循环，即：下面的代码都不会执行
                    }
                }

                // 全部移除
                jwtHashSet.add(jwtUserKey); // 添加元素：jwtUserKey
                removeKeySet.addAll(jwtHashSet); // 添加所有到：removeKeySet
            }

        }

        if (removeKeySet.size() != 0) {
            redisTemplate.delete(removeKeySet); // 执行批量移除
        }

    }

    /**
     * jwtHashSet，随机剩下一个
     */
    private static void jwtHashSetAnyOne(BoundSetOperations<String, String> setOps, Set<String> jwtHashSet,
        Set<String> removeKeySet) {

        boolean flag = false;

        Set<String> jwtHashRemoveSet = new HashSet<>();
        for (String subItem : jwtHashSet) {
            if (flag) {
                jwtHashRemoveSet.add(subItem); // 添加到：jwtHashRemoveSet 里
            } else {
                flag = true; // 赋值为 true，表示已经 随机剩了一个
            }
        }
        if (jwtHashRemoveSet.size() != 0) {
            if (removeKeySet != null) {
                removeKeySet.addAll(jwtHashRemoveSet); // 移除 jwtHash
            }
            setOps.remove(jwtHashRemoveSet.toArray());
        }

    }

    /**
     * 判断：jwtUserKey是否存在于 redis中
     */
    private static String jwtUserKeyIsExist(String jwtUserKeyPre, RequestCategoryEnum item) {

        String jwtUserKey = jwtUserKeyPre + item.getCode();

        Boolean hasKey = redisTemplate.hasKey(jwtUserKey);
        if (hasKey == null || !hasKey) {
            return null;
        }

        return jwtUserKey;
    }

    /**
     * 获取用户 jwt私钥后缀，通过 userId
     */
    public static String getUserJwtSecretSufByUserId(Long userId) {
        if (userId == null || BaseConstant.ADMIN_ID.equals(userId)) {
            return null;
        }

        BaseUserSecurityDO baseUserSecurityDO = UserUtil.getUserJwtSecretSufByUserId(userId);
        if (baseUserSecurityDO == null || StrUtil.isBlank(baseUserSecurityDO.getJwtSecretSuf())) {
            return null;
        }

        return baseUserSecurityDO.getJwtSecretSuf();
    }

    /**
     * 通过 userId获取到权限的 Set
     */
    public static List<SimpleGrantedAuthority> getAuthSetByUserId(Long userId) {

        if (userId == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST); // 直接抛出异常
            return null;
        }

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            return null; // admin自带所有权限
        }

        List<SimpleGrantedAuthority> result = new ArrayList<>(); // 本方法返回值

        List<BaseMenuDO> menuList = UserUtil.getMenuListByUserId(userId, 2); // 通过用户 id，获取 菜单集合

        if (menuList.size() == 0) {
            return result;
        }

        Set<String> authsSet = menuList.stream().map(BaseMenuDO::getAuths).collect(Collectors.toSet());

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

        result = hashSet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return result;
    }

}
