package com.ktb.redis.cache;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Lua 스크립트를 Spring Cache 키 직렬화와 함께 실행할 수 있는 RedisCache.
 * <p>
 * {@link LuaRedisCache}를 상속하며, key prefix 관리는 {@link com.ktb.redis.policy.CachePolicy}에 위임한다.
 * {@code execute()} 메서드는 캐시 이름 prefix가 붙은 키를 자동으로 생성하여 스크립트에 전달한다.
 */
@Slf4j
public class ScriptableRedisCache extends LuaRedisCache {

    public ScriptableRedisCache(String name, RedisCacheWriter cacheWriter,
                                RedisCacheConfiguration cacheConfig,
                                RedisConnectionFactory connectionFactory, Duration ttl) {
        super(name, cacheWriter, cacheConfig, connectionFactory, ttl);
    }

    /**
     * Lua 스크립트를 실행한다.
     * <p>
     * KEYS[1]에는 {@code cacheName::key} 형식의 직렬화된 키가 전달되며,
     * ARGV[1..N]에는 {@code scriptArgs}가 순서대로 전달된다.
     *
     * @param script     실행할 Lua 스크립트
     * @param returnType Redis 반환 타입 (INTEGER, VALUE 등)
     * @param key        캐시 키 (cache name prefix가 자동 추가됨)
     * @param scriptArgs 스크립트 ARGV 인자 목록
     * @param <T>        반환 타입
     * @return 스크립트 실행 결과, 실패 시 null
     */
    @SuppressWarnings("unchecked")
    public <T> T execute(RedisScript<T> script, ReturnType returnType, Object key, String... scriptArgs) {
        byte[] k = serializeCacheKey(createCacheKey(key));
        byte[] scriptBytes = script.getScriptAsString().getBytes(StandardCharsets.UTF_8);
        byte[][] argBytes = Arrays.stream(scriptArgs)
                .map(s -> s.getBytes(StandardCharsets.UTF_8))
                .toArray(byte[][]::new);
        byte[][] keysAndArgs = Stream.concat(Stream.of(k), Arrays.stream(argBytes))
                .toArray(byte[][]::new);
        try (var conn = connectionFactory.getConnection()) {
            return (T) conn.eval(scriptBytes, returnType, 1, keysAndArgs);
        } catch (Exception e) {
            log.warn("[Cache] execute failed - cache={} key={}", getName(), key, e);
            return null;
        }
    }
}
