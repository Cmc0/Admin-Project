package com.cmc.common.configuration;

import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RedissonClientConfiguration {

    @Resource
    RedisProperties redisProperties;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {

        Config config = new Config();

        String redisPassword = null;
        if (StrUtil.isNotBlank(redisProperties.getPassword())) {
            redisPassword = redisProperties.getPassword();
        }

        config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
            .setPassword(redisPassword);

        return Redisson.create(config);
    }
}
