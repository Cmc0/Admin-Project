package com.cmc.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;

@Configuration
public class RedisTemplateConfiguration {

    @Resource
    RedisConnectionFactory factory;

    /**
     * 全局设置 RedisTemplate的序列化方式，目的：不然 redis中会有些奇怪的前缀，因为默认使用的是 jdk序列化
     */
    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        RedisSerializer<String> string = RedisSerializer.string();
        RedisSerializer<Object> json = RedisSerializer.json();
        redisTemplate.setKeySerializer(string);
        redisTemplate.setValueSerializer(json);
        redisTemplate.setHashKeySerializer(string);
        redisTemplate.setHashValueSerializer(json);
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

}
