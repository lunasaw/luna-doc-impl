package io.github.lunasaw.lock.luna;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LockUtil {
    private static final Logger           LOGGER              = LoggerFactory.getLogger(LockUtil.class);

    private static final String           RELEASE_LOCK_SCRIPT =
        "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 尝试获取锁
     *
     * @param key
     * @param value
     * @param expire
     * @param tryTimes 最大尝试次数
     * @param interval 间隔时长，毫秒
     * @return
     */
    public boolean tryLock(String key, String value, int expire, int tryTimes, long interval) {
        try {
            while (tryTimes > 0) {
                if (getLock(key, value, expire)) {
                    return true;
                }
                Thread.sleep(interval);
                tryTimes--;
            }
        } catch (Exception e) {
            LOGGER.error("LockUtil.tryLock error", e);
        }
        return false;
    }

    /**
     * 获取锁
     *
     * @param key
     * @param value 禁止使用固定值
     * @param expire
     * @return
     */
    public boolean getLock(String key, String value, int expire) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, expire, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error("LockUtil.getLock error", e);
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param key
     * @param value
     * @return
     */
    public boolean releaseLock(String key, String value) {
        try {
            Long result = redisTemplate.execute((RedisCallback<Long>)connection -> (Long)connection.eval(RELEASE_LOCK_SCRIPT.getBytes(),
                ReturnType.VALUE, 1, key.getBytes(), value.getBytes()));
            return Objects.equals(1L, result);
        } catch (Exception e) {
            LOGGER.error("LockUtil.releaseLock error", e);
            return false;
        }
    }
}
