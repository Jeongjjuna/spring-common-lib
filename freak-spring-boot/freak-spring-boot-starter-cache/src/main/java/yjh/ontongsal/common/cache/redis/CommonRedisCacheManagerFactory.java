package yjh.ontongsal.common.cache.redis;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import yjh.ontongsal.common.cache.CacheDefinition;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommonRedisCacheManagerFactory {

    private CommonRedisCacheManagerFactory() {
    }

    public static <E extends Enum<E> & CacheDefinition> RedisCacheManager from(
            RedisConnectionFactory connectionFactory,
            Class<E> definitionClass, String namespace
    ) {
        var configs = Arrays.stream(definitionClass.getEnumConstants())
                .collect(Collectors.toMap(
                        CacheDefinition::cacheName,
                        def -> RedisCacheConfiguration.defaultCacheConfig()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new StringRedisSerializer()
                                ))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(def.valueType())
                                ))
                                .disableCachingNullValues()
                                .entryTtl(def.ttl())
                                .computePrefixWith(name -> namespace + ":" + name + ":")
                ));
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
