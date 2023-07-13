package io.github.lunasaw.lock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisValueCache {

    @Autowired
    private RedisTemplate redisTemplate;

    public void cache(final String key, final Object data) {
        redisTemplate.opsForValue().set(key, data);
    }

    public Object getCachedValue(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteCachedValue(final String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }
}