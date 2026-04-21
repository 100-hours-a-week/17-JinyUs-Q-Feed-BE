package com.ktb.redis.config;

import com.ktb.redis.cache.QFeedCacheManager;
import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public RedisCacheConfiguration defaultRedisCacheConfiguration(
            RedisSerializationContext.SerializationPair<String> redisKeySerializer,
            RedisSerializationContext.SerializationPair<Object> redisValueSerializer
    ) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(redisKeySerializer)
                .serializeValuesWith(redisValueSerializer)
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();
    }

    @Bean
    public RedisCacheWriter redisCacheWriter(RedisConnectionFactory connectionFactory) {
        return RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
    }

    @Bean
    public QFeedCacheManager qFeedCacheManager(
            RedisCacheWriter redisCacheWriter,
            RedisCacheConfiguration defaultRedisCacheConfiguration,
            RedisConnectionFactory connectionFactory
    ) {
        return new QFeedCacheManager(redisCacheWriter, defaultRedisCacheConfiguration, connectionFactory);
    }
}
