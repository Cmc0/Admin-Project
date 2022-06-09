package com.admin.common.configuration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class JsonRedisTemplate<T> extends RedisTemplate<String, T> {

    public JsonRedisTemplate(RedisConnectionFactory connectionFactory) {
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(RedisSerializer.json());
        setHashKeySerializer(RedisSerializer.string());
        setHashValueSerializer(RedisSerializer.json());
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
