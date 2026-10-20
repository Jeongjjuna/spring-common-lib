package yjh.ontongsal.springbootexample.cache;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserInfo(
        Long userId,
        String name,
        String email,
        Integer age,
        Boolean active,
        LocalDate birthDate,
        LocalDateTime createdAt,
        Instant lastLoginAt,
        BigDecimal points,
        List<String> roles
) {
}