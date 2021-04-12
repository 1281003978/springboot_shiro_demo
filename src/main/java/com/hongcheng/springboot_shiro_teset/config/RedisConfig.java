package com.hongcheng.springboot_shiro_teset.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	/**
     * 	redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
     * 	如果没有用的redis，那么这个类可以删掉
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置value的序列化规则和 key的序列化规则
        /**
         * 	如果用GenericJackson2JsonRedisSerializer来序列号shiro的session，会出现反序列化失败的情况
         * 	可以考虑使用别的方法进行序列化，session应该要进行加密在保存
         * 	我这里为了方便，直接用jdk的序列化
         * */
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(RedisSerializer.java());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(RedisSerializer.java());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    
    
    
}
