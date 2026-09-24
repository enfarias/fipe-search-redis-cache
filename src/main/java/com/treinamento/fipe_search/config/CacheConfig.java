package com.treinamento.fipe_search.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private final RedisConnectionFactory connectionFactory;
    private final String cacheName;
    private final long ttlHours;

    public CacheConfig(
            RedisConnectionFactory connectionFactory,
            @Value("${app.cache.name}") String cacheName,
            @Value("${app.cache.ttl-hours:24}") long ttlHours
    ) {
        this.connectionFactory = connectionFactory;
        this.cacheName = cacheName;
        this.ttlHours = ttlHours;
    }

    @Override
    public CacheManager cacheManager() {
        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(ttlHours));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }

    @Override
    public CacheResolver cacheResolver() {
        return context -> context.getOperation().getCacheNames().stream()
                .map(name -> cacheManager().getCache(name))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> String.join(":", Arrays.stream(params)
                .map(String::valueOf)
                .toList());
    }
}