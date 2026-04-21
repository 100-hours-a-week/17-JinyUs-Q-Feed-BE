package com.ktb.redis.cache;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.Nullable;

@Slf4j
public class LuaRedisCache extends RedisCache {

    private static final RedisScript<Object> LUA_SET = RedisScript.of(
            "redis.call('SET', KEYS[1], ARGV[1])\n"
            + "redis.call('PEXPIRE', KEYS[1], ARGV[2])\n"
            + "return nil",
            Object.class);

    private static final RedisScript<byte[]> LUA_SET_NX = RedisScript.of(
            "if redis.call('SETNX', KEYS[1], ARGV[1]) == 1 then\n"
            + "  redis.call('PEXPIRE', KEYS[1], ARGV[2])\n"
            + "  return nil\n"
            + "end\n"
            + "return redis.call('GET', KEYS[1])",
            byte[].class);

    protected final RedisConnectionFactory connectionFactory;
    protected final Duration ttl;

    public LuaRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig,
                         RedisConnectionFactory connectionFactory, Duration ttl) {
        super(name, cacheWriter, cacheConfig);
        this.connectionFactory = connectionFactory;
        this.ttl = ttl;
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            return;
        }

        Object storeValue = toStoreValue(value);
        try {
            byte[] k = serializeCacheKey(createCacheKey(key));
            byte[] v = serializeCacheValue(storeValue);
            executeScript(LUA_SET, ReturnType.VALUE, List.of(k),
                    v, String.valueOf(ttl.toMillis()).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.warn("[Cache] put failed - cache={} key={}", getName(), key, e);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        if (value == null) {
            return get(key);
        }
        Object storeValue = toStoreValue(value);
        try {
            byte[] k = serializeCacheKey(createCacheKey(key));
            byte[] v = serializeCacheValue(storeValue);
            byte[] existing = executeScript(LUA_SET_NX, ReturnType.VALUE, List.of(k),
                    v, String.valueOf(ttl.toMillis()).getBytes(StandardCharsets.UTF_8));
            if (existing != null) {
                Object existingValue = deserializeCacheValue(existing);
                return () -> fromStoreValue(existingValue);
            }
            return null;
        } catch (Exception e) {
            log.warn("[Cache] putIfAbsent failed - cache={} key={}", getName(), key, e);
            return null;
        }
    }

    @Override
    protected Object lookup(Object key) {
        try {
            return super.lookup(key);
        } catch (Exception e) {
            log.warn("[Cache] lookup failed - cache={} key={}", getName(), key, e);
            return null;
        }
    }

    protected byte[] executeScript(RedisScript<?> script, ReturnType returnType,
                                   List<byte[]> keys, byte[]... args) {
        byte[] scriptBytes = script.getScriptAsString().getBytes(StandardCharsets.UTF_8);
        byte[][] keysAndArgs = Stream.concat(keys.stream(), Arrays.stream(args))
                .toArray(byte[][]::new);
        try (RedisConnection conn = connectionFactory.getConnection()) {
            return (byte[]) conn.eval(scriptBytes, returnType, keys.size(), keysAndArgs);
        }
    }
}
