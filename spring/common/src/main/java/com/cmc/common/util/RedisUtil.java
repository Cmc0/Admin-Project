package com.cmc.common.util;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * redis 工具类
 */
@Component
public class RedisUtil {

    private static RedisTemplate<String, Object> redisTemplate;

    @Resource
    private void setRedisTemplate(RedisTemplate<String, Object> value) {
        redisTemplate = value;
    }

    /**
     * scan获取 redis所有 key，通过 模糊匹配 matchKey
     */
    public static Set<String> scan(String matchKey) {

        return redisTemplate.execute((RedisCallback<Set<String>>)connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("*" + matchKey + "*").build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
            }
            return keysTmp;
        });

    }
}
