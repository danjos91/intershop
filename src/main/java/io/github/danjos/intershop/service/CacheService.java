package io.github.danjos.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);
    
    public <T> Mono<T> get(String key, Class<T> clazz) {
        return redisTemplate.opsForValue().get(key)
                .map(obj -> (T) obj)
                .doOnNext(item -> log.debug("Cache hit for key: {}", key))
                .doOnError(error -> log.warn("Cache error for key: {}: {}", key, error.getMessage()));
    }
    
    public <T> Mono<Boolean> set(String key, T value) {
        return set(key, value, DEFAULT_TTL);
    }
    
    public <T> Mono<Boolean> set(String key, T value, Duration ttl) {
        return redisTemplate.opsForValue().set(key, value, ttl)
                .doOnSuccess(success -> {
                    if (success) {
                        log.debug("Cached item with key: {} for {} seconds", key, ttl.getSeconds());
                    }
                })
                .doOnError(error -> log.warn("Cache set error for key: {}: {}", key, error.getMessage()));
    }
    
    public Mono<Boolean> delete(String key) {
        return redisTemplate.delete(key)
                .map(count -> count > 0)
                .doOnSuccess(deleted -> {
                    if (deleted) {
                        log.debug("Deleted cache key: {}", key);
                    }
                });
    }
    
    public Mono<Boolean> exists(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public Mono<Long> deletePattern(String pattern) {
        return redisTemplate.delete(redisTemplate.keys(pattern))
                .doOnSuccess(count -> log.debug("Deleted {} cache keys matching pattern: {}", count, pattern));
    }
    
    public <T> Mono<Boolean> setList(String key, List<T> items) {
        return setList(key, items, DEFAULT_TTL);
    }
    
    public <T> Mono<Boolean> setList(String key, List<T> items, Duration ttl) {
        return redisTemplate.opsForValue().set(key, items, ttl)
                .doOnSuccess(success -> {
                    if (success) {
                        log.debug("Cached list with key: {} for {} seconds", key, ttl.getSeconds());
                    }
                });
    }
    
    public <T> Mono<List<T>> getList(String key, Class<T> clazz) {
        return redisTemplate.opsForValue().get(key)
                .map(obj -> (List<T>) obj)
                .doOnNext(items -> log.debug("Cache hit for list key: {}", key))
                .doOnError(error -> log.warn("Cache error for list key: {}: {}", key, error.getMessage()));
    }
}
