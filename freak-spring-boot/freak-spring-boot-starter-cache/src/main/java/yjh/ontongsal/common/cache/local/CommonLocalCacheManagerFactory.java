package yjh.ontongsal.common.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import yjh.ontongsal.common.cache.CacheDefinition;

import java.util.Arrays;

public class CommonLocalCacheManagerFactory {

    private CommonLocalCacheManagerFactory() {
    }

    public static <E extends Enum<E> & CacheDefinition> SimpleCacheManager from(Class<E> definitionClass, long maximumSize) {
        var caches = Arrays.stream(definitionClass.getEnumConstants())
                .map(def -> new CaffeineCache(
                        def.cacheName(),
                        Caffeine.newBuilder()
                                .expireAfterWrite(def.ttl())
                                .maximumSize(maximumSize)
                                .recordStats()
                                .build()
                ))
                .toList();
        var manager = new SimpleCacheManager();
        manager.setCaches(caches);
        return manager;
    }
}
