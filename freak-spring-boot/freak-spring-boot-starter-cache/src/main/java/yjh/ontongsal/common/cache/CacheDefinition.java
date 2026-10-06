package yjh.ontongsal.common.cache;

import java.time.Duration;

public interface CacheDefinition {
    String cacheName();

    Duration ttl();

    Class<?> valueType();
}
