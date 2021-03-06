package com.admin.common.configuration;

import cn.hutool.core.util.IdUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import javax.annotation.Resource;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Resource
    KafkaProperties kafkaProperties;

    @Value("${server.port:8080}")
    public Integer port;

    /**
     * 把 groupId 设置成随机的，目的：可以实现订阅 topic
     * 注意：如果 @KafkaListener 注解，指定了 id或者 groupId，则随机 groupId则会变成指定值，即：被 @KafkaListener配置的参数覆盖掉
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> dynamicGroupIdContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        String groupId; // 设置：groupId 为 hostname + port，或者 uuid
        try {
            groupId = BaseConfiguration.HOST_NAME + ":" + port;
        } catch (Exception e) {
            groupId = IdUtil.simpleUUID();
        }

        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();

        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // 这里设置为 自动提交

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerProperties));

        return factory;
    }

}
