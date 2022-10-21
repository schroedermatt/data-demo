package org.msse.demo;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@EnableCaching
@Configuration
public class RedisConfig {
    @Bean
    RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory);
        builder.enableStatistics();
        return builder.build();
    }

    @Bean
    RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);
        // redisStandaloneConfiguration.setPassword(RedisPassword.of("password"));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
