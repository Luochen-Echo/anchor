package com.gov.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ///设置String类型的key设置序列化器
        template.setKeySerializer(new StringRedisSerializer());

        //设置Hash类型的key设置序列化器
        template.setHashKeySerializer(new StringRedisSerializer());


        // 设置 value 的序列化器
        template.setValueSerializer(new FastJson2JsonRedisSerializer<>(Object.class));

        return template;
    }

}