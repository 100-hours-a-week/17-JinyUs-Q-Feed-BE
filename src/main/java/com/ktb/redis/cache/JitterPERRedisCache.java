package com.ktb.redis.cache;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.Nullable;

@Slf4j
public class JitterPERRedisCache extends LuaRedisCache {

    private static final int JITTER_PERCENT = 10;
    private static final double PER_BETA = 1.0;
    private static final long PER_DELTA_MS = 100L;
    private static final String PER_META_SUFFIX = ":per";

    private static final RedisScript<Object> LUA_SET_WITH_PER = RedisScript.of(
            "redis.call('SET', KEYS[1], ARGV[1], 'PX', ARGV[2])\n"
            + "redis.call('SET', KEYS[2], ARGV[3], 'PX', ARGV[2])\n"
            + "return nil",
            Object.class);

    public JitterPERRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig,
                               RedisConnectionFactory connectionFactory, Duration ttl) {
        super(name, cacheWriter, cacheConfig, connectionFactory, ttl);
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            return;
        }

        Object storeValue = toStoreValue(value);
        try {
            Duration jittered = computeJitteredTtl();
            long expiresAt = System.currentTimeMillis() + jittered.toMillis();
            byte[] k = serializeCacheKey(createCacheKey(key));
            byte[] metaK = serializeCacheKey(createCacheKey(key) + PER_META_SUFFIX);
            byte[] v = serializeCacheValue(storeValue);
            executeScript(LUA_SET_WITH_PER, ReturnType.VALUE, List.of(k, metaK),
                    v,
                    String.valueOf(jittered.toMillis()).getBytes(StandardCharsets.UTF_8),
                    String.valueOf(expiresAt).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.warn("[Cache] put failed - cache={} key={}", getName(), key, e);
        }
    }

    @Override
    protected Object lookup(Object key) {
        Object value = super.lookup(key);
        if (value == null) {
            return null;
        }
        byte[] metaK = serializeCacheKey(createCacheKey(key) + PER_META_SUFFIX);
        try (RedisConnection conn = connectionFactory.getConnection()) {
            byte[] raw = conn.get(metaK);
            if (raw != null) {
                long expiresAt = Long.parseLong(new String(raw, StandardCharsets.UTF_8));
                if (isPERTriggered(expiresAt)) {
                    log.debug("[PER] early recompute triggered - cache={} key={}", getName(), key);
                    return null;
                }
            }
        } catch (Exception e) {
            log.warn("[Cache] PER meta lookup failed - cache={} key={}", getName(), key, e);
        }
        return value;
    }

    private boolean isPERTriggered(long expiresAt) {
        double random = ThreadLocalRandom.current().nextDouble();
        if (random <= 0) {
            return false;
        }
        return System.currentTimeMillis() - PER_BETA * PER_DELTA_MS * Math.log(random) > expiresAt;
    }

    private Duration computeJitteredTtl() {
        double r = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        long jitterMs = (long) (ttl.toMillis() * JITTER_PERCENT / 100.0 * r);
        return Duration.ofMillis(Math.max(1_000L, ttl.toMillis() + jitterMs));
    }
}
