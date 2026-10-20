package yjh.ontongsal.springbootexample.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserCacheRepository {

    @Cacheable(cacheManager = "CaffeineCacheManager", cacheNames = "USER_INFO", key = "#userId")
    public UserInfo getUserInfoFromLocalCache(Long userId) {
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

    @Cacheable(cacheManager = "RedisCacheManager", cacheNames = "USER_INFO", key = "#userId")
    public UserInfo getUserInfoFromGlobalCache(Long userId) {
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
}
