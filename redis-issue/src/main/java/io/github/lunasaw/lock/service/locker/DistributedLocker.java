package io.github.lunasaw.lock.service.locker;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DistributedLocker {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final long   DEFAULT_RETRY_TIME = 100L;

    public <T> LockExecutionResult<T> lock(final String key,
        final int howLongShouldLockBeAcquiredSeconds,
        final int lockTimeoutSeconds,
        final Callable<T> task) {
        try {
            return tryToGetLock(() -> {
                final Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(key, key, lockTimeoutSeconds, TimeUnit.SECONDS);
                if (lockAcquired == Boolean.FALSE) {
                    log.error("Failed to acquire lock for key '{}'", key);
                    return null;
                }

                log.info("Successfully acquired lock for key '{}'", key);

                try {
                    T taskResult = task.call();
                    return LockExecutionResult.buildLockAcquiredResult(taskResult);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return LockExecutionResult.buildLockAcquiredWithException(e);
                } finally {
                    releaseLock(key);
                }
            }, key, howLongShouldLockBeAcquiredSeconds);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return LockExecutionResult.buildLockAcquiredWithException(e);
        }
    }

    private void releaseLock(final String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    private static <T> T tryToGetLock(final Supplier<T> task,
        final String lockKey,
        final int howLongShouldLockBeAcquiredSeconds) throws Exception {
        final long tryToGetLockTimeout = TimeUnit.SECONDS.toMillis(howLongShouldLockBeAcquiredSeconds);

        final long startTimestamp = System.currentTimeMillis();
        while (true) {
            log.info("Trying to get the lock with key '{}'", lockKey);
            final T response = task.get();
            if (response != null) {
                return response;
            }
            sleep(DEFAULT_RETRY_TIME);

            if (System.currentTimeMillis() - startTimestamp > tryToGetLockTimeout) {
                throw new Exception("Failed to acquire lock in " + tryToGetLockTimeout + " milliseconds");
            }
        }
    }

    private static void sleep(final long sleepTimeMillis) {
        try {
            Thread.sleep(sleepTimeMillis);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
    }
}