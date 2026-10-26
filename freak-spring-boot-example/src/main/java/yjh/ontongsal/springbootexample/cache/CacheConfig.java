package yjh.ontongsal.springbootexample.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import yjh.ontongsal.common.cache.LocalLockCacheStampedeProtector;
import yjh.ontongsal.common.cache.caffeine.CommonCaffeineCacheManagerFactory;
import yjh.ontongsal.common.cache.redis.CommonRedisCacheManagerFactory;

@EnableCaching
@Configuration
public class CacheConfig {

    @Primary
    @Bean("CaffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        return CommonCaffeineCacheManagerFactory.from(UserCacheDefinition.class, 1000L);
    }

    @Bean("RedisCacheManager")
    public CacheManager redisCacheManager(
            RedisConnectionFactory redisConnectionFactory
    ) {
        return CommonRedisCacheManagerFactory.from(
                redisConnectionFactory,
                UserCacheDefinition.class,
                "USER_API");
    }

    @Bean
    public LocalLockCacheStampedeProtector localLockCacheStampedeProtector() {
        return new LocalLockCacheStampedeProtector(256);
    }
}
