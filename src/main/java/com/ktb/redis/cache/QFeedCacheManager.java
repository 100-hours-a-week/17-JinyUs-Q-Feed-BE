package com.ktb.redis.cache;

import com.ktb.redis.policy.CachePolicy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import lombok.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

public class QFeedCacheManager extends AbstractTransactionSupportingCacheManager {

    private final RedisCacheWriter cacheWriter;
    private final RedisCacheConfiguration defaultConfig;
    private final RedisConnectionFactory connectionFactory;

    public QFeedCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultConfig,
                             RedisConnectionFactory connectionFactory) {
        this.cacheWriter = cacheWriter;
        this.defaultConfig = defaultConfig;
        this.connectionFactory = connectionFactory;
        setTransactionAware(true);
    }

    @Override
    protected @NonNull Collection<? extends Cache> loadCaches() {
        return Arrays.stream(CachePolicy.values())
                .map(this::createCache)
                .toList();
    }

    private Cache createCache(CachePolicy policy) {
        Duration ttl = policy.getTtl().getDuration();
        RedisCacheConfiguration config = defaultConfig.entryTtl(ttl);
        if (policy.isHotKey()) {
            return new JitterPERRedisCache(policy.getCacheName(), cacheWriter, config, connectionFactory, ttl);
        }
        return new LuaRedisCache(policy.getCacheName(), cacheWriter, config, connectionFactory, ttl);
    }
}
