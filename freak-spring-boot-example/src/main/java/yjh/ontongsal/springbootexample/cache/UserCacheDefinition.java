package yjh.ontongsal.springbootexample.cache;

import yjh.ontongsal.common.cache.CacheDefinition;

import java.time.Duration;

public enum UserCacheDefinition implements CacheDefinition {
    USER_INFO("USER_INFO", Duration.ofSeconds(5), UserInfo.class);

    private final String cacheName;
    private final Duration ttl;
    private final Class<?> valueType;

    UserCacheDefinition(String cacheName, Duration ttl, Class<?> valueType) {
        this.cacheName = cacheName;
        this.ttl = ttl;
        this.valueType = valueType;
    }

    @Override
    public String cacheName() {
        return cacheName;
    }

    @Override
    public Duration ttl() {
        return ttl;
    }

    @Override
    public Class<?> valueType() {
        return valueType;
    }
}
