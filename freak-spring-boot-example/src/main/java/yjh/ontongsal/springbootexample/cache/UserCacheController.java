package yjh.ontongsal.springbootexample.cache;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/cache")
public class UserCacheController {

    private final UserCacheRepository userCacheRepository;
    private final UserCacheStampedProtectRepository userCacheStampedProtectRepository;

    public UserCacheController(UserCacheRepository userCacheRepository, UserCacheStampedProtectRepository userCacheStampedProtectRepository) {
        this.userCacheRepository = userCacheRepository;
        this.userCacheStampedProtectRepository = userCacheStampedProtectRepository;
    }

    @GetMapping("/{userId}/non-with-cachestamped-protector")
    public UserInfo getUserWithStampedProtector(@PathVariable Long userId) {
        return userCacheStampedProtectRepository.getUserNonWithCacheStampedProtectorFromGlobalCache(userId);
    }

    @GetMapping("/{userId}/with-cachestamped-protector")
    public UserInfo getUserNonWithStampedProtector(@PathVariable Long userId) {
        return userCacheStampedProtectRepository.getUserInfoFromGlobalCache(userId);
    }
}
