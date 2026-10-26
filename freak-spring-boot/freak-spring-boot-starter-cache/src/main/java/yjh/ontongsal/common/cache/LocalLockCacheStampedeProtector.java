package yjh.ontongsal.common.cache;

import org.springframework.cache.Cache;

import java.util.Objects;
import java.util.function.Supplier;


public class LocalLockCacheStampedeProtector {

    private final Object[] locks;

    public LocalLockCacheStampedeProtector(int lockCount) {
        if (lockCount <= 0) {
            throw new IllegalArgumentException(
                    "lockCount must be greater than 0"
            );
        }

        this.locks = createLocks(lockCount);
    }

    public <T> T get(Cache cache, Object key, Supplier<T> loader) {
        Objects.requireNonNull(cache, "cache must not be null");
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(loader, "loader must not be null");

        T cached = getCachedValue(cache, key);
        if (cached != null) {
            return cached;
        }

        Object lock = getLock(cache, key);

        synchronized (lock) {
            cached = getCachedValue(cache, key);
            if (cached != null) {
                return cached;
            }

            T value = loader.get();

            if (value != null) {
                cache.put(key, value);
            }

            return value;
        }
    }

    private Object getLock(Cache cache, Object key) {
        int hash = 31 * System.identityHashCode(cache) + key.hashCode();
        hash ^= hash >>> 16;

        return locks[Math.floorMod(hash, locks.length)];
    }

    private static Object[] createLocks(int lockCount) {
        Object[] locks = new Object[lockCount];

        for (int i = 0; i < lockCount; i++) {
            locks[i] = new Object();
        }

        return locks;
    }

    @SuppressWarnings("unchecked")
    private <T> T getCachedValue(Cache cache, Object key) {
        Cache.ValueWrapper wrapper = cache.get(key);

        return wrapper == null ? null : (T) wrapper.get();
    }
}