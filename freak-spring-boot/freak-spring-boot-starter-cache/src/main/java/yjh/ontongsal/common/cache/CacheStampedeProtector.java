package yjh.ontongsal.common.cache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public final class CacheStampedeProtector {

    private static final ConcurrentMap<Object, CompletableFuture<Object>> IN_FLIGHT =
            new ConcurrentHashMap<>();

    private CacheStampedeProtector() {
    }

    public static <K, V> V load(
            K key,
            Supplier<V> loader
    ) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        CompletableFuture<Object> existing = IN_FLIGHT.putIfAbsent(key, future);
        if (existing != null) {
            // 같은 키의 작업이 진행 중이면 결과를 공유한다.
            try {
                return cast(existing.join());
            } catch (CompletionException e) {
                throw propagate(e.getCause());
            }
        }

        try {
            V value = loader.get();
            future.complete(value);
            return value;
        } catch (Throwable e) {
            future.completeExceptionally(e);
            throw propagate(e);
        } finally {
            IN_FLIGHT.remove(key, future);
        }
    }

    @SuppressWarnings("unchecked")
    private static <V> V cast(Object value) {
        return (V) value;
    }

    private static RuntimeException propagate(Throwable cause) {
        if (cause instanceof Error e) {
            throw e;
        }

        if (cause instanceof RuntimeException e) {
            return e;
        }

        return new IllegalStateException(cause);
    }

}