package com.admin.common.util;

import cn.hutool.core.convert.Convert;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.mapper.SysParamMapper;
import com.admin.common.model.constant.BaseRedisConstant;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysParamDO;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统参数 工具类
 */
@Component
public class SysParamUtil {

    private static SysParamMapper sysParamMapper;

    @Resource
    private void setParamMapper(SysParamMapper value) {
        sysParamMapper = value;
    }

    private static JsonRedisTemplate<String> jsonRedisTemplate;

    @Resource
    private void setJsonRedisTemplate(JsonRedisTemplate<String> value) {
        jsonRedisTemplate = value;
    }

    private static RedissonClient redissonClient;

    @Resource
    private void setRedissonClient(RedissonClient value) {
        redissonClient = value;
    }

    /**
     * 通过主键 id，获取 value，没有 value则返回 null
     */
    public static String getValueById(Long id) {

        String idStr = Convert.toStr(id);
        if (idStr == null) {
            return null;
        }

        Boolean hasKey = jsonRedisTemplate.hasKey(BaseRedisConstant.PRE_REDIS_PARAM_CACHE);

        if (hasKey != null && hasKey) {
            BoundHashOperations<String, String, String> ops =
                jsonRedisTemplate.boundHashOps(BaseRedisConstant.PRE_REDIS_PARAM_CACHE);
            return ops.get(idStr);
        }

        // 更新 redis中【系统参数】的缓存，并返回 值，备注：返回值可能会为 null
        return updateRedisCache(true).get(idStr);
    }

    /**
     * 更新 redis中【系统参数】的缓存，并返回 值
     */
    public static Map<String, String> updateRedisCache(boolean lockFlag) {

        RLock lock = null;
        if (lockFlag) {
            lock = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + BaseRedisConstant.PRE_REDIS_PARAM_CACHE);
            lock.lock();
        }

        try {
            List<SysParamDO> paramRedisList =
                ChainWrappers.lambdaQueryChain(sysParamMapper).select(BaseEntityTwo::getId, SysParamDO::getValue)
                    .eq(BaseEntityThree::getEnableFlag, true).list();

            // 转换为 map，目的：提供速度
            // 注意：Collectors.toMap()方法，key不能重复，不然会报错
            // 可以用第三个参数，解决这个报错：(v1, v2) -> v2 不覆盖（留前值）(v1, v2) -> v1 覆盖（取后值）
            Map<String, String> map =
                paramRedisList.stream().collect(Collectors.toMap(it -> it.getId().toString(), SysParamDO::getValue));

            jsonRedisTemplate.delete(BaseRedisConstant.PRE_REDIS_PARAM_CACHE);
            jsonRedisTemplate.boundHashOps(BaseRedisConstant.PRE_REDIS_PARAM_CACHE).putAll(map);

            return map;
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

}
