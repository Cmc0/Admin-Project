package com.admin.common.util;

import com.admin.common.configuration.JsonRedisTemplate;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
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

    private static JsonRedisTemplate<Object> jsonRedisTemplate;

    @Resource
    private void setJsonRedisTemplate(JsonRedisTemplate<Object> value) {
        jsonRedisTemplate = value;
    }

    /**
     * scan获取 redis所有 key，通过 模糊匹配 matchKey
     */
    public static Set<String> scan(String matchKey) {

        return jsonRedisTemplate.execute((RedisCallback<Set<String>>)connection -> {
            Set<String> keySet = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("*" + matchKey + "*").build());
            while (cursor.hasNext()) {
                keySet.add(new String(cursor.next()));
            }
            return keySet;
        });

    }
}
