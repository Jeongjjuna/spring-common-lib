package yjh.ontongsal.springbootexample.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import yjh.ontongsal.common.cache.CacheStampedeProtector;
import yjh.ontongsal.common.cache.LocalLockCacheStampedeProtector;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
public class UserCacheStampedProtectRepository {
    private final CacheManager redisCacheManager;
    private final LocalLockCacheStampedeProtector localLockCacheStampedeProtector;

    public UserCacheStampedProtectRepository(
            CacheManager redisCacheManager,
            LocalLockCacheStampedeProtector localLockCacheStampedeProtector
    ) {
        this.redisCacheManager = redisCacheManager;
        this.localLockCacheStampedeProtector = localLockCacheStampedeProtector;
    }

    @Cacheable(cacheManager = "RedisCacheManager", cacheNames = "USER_INFO", key = "#userId")
    public UserInfo getUserNonWithCacheStampedProtectorFromGlobalCache(Long userId) {
        IO.println("DB FALLBACK");
        return new UserInfo(
                1L,
                "홍길동",
                "hong@example.com",
                30,
                true,
                LocalDate.of(1996, 5, 15),
                LocalDateTime.of(2025, 1, 10, 9, 30, 0),
                Instant.parse("2026-10-20T12:00:00Z"),
                new BigDecimal("15000.50"),
                List.of("USER", "ADMIN")
        );
    }

    /**
     * Redis 캐시 매니저 구현체에 대해서는, CacheStampedeProtector 를 통해 스탬피드를 방지한다.
     * Cache Miss 에 대한 동시성 경합을 줄여주는 역할. (완벽하게 1번 실행을 보장하진 못한다)
     */
    @Cacheable(cacheManager = "RedisCacheManager", cacheNames = "USER_INFO", key = "#userId")
    public UserInfo getUserInfoFromGlobalCache(Long userId) {
        return CacheStampedeProtector.load(
                userId,
                () -> {
                    IO.println("DB FALLBACK");
                    return new UserInfo(
                            1L,
                            "홍길동",
                            "hong@example.com",
                            30,
                            true,
                            LocalDate.of(1996, 5, 15),
                            LocalDateTime.of(2025, 1, 10, 9, 30, 0),
                            Instant.parse("2026-10-20T12:00:00Z"),
                            new BigDecimal("15000.50"),
                            List.of("USER", "ADMIN")
                    );
                }
        );
    }

    public UserInfo getUserInfoFromGlobalCache2(Long userId) {
        Cache cache = Objects.requireNonNull(
                redisCacheManager.getCache(UserCacheDefinition.USER_INFO.cacheName())
        );

        return localLockCacheStampedeProtector.get(
                cache,
                userId,
                () -> {
                    IO.println("DB FALLBACK");
                    return new UserInfo(
                            1L,
                            "홍길동",
                            "hong@example.com",
                            30,
                            true,
                            LocalDate.of(1996, 5, 15),
                            LocalDateTime.of(2025, 1, 10, 9, 30, 0),
                            Instant.parse("2026-10-20T12:00:00Z"),
                            new BigDecimal("15000.50"),
                            List.of("USER", "ADMIN")
                    );
                }
        );
    }
}
