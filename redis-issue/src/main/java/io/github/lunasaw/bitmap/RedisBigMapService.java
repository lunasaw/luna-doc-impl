package io.github.lunasaw.bitmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 日活统计
 * 点赞
 * 布隆过滤
 * 
 * @author weidian
 * @date 2023/7/26
 */
@Component
public class RedisBigMapService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 统计计数
     *
     * @param key
     * @return
     */
    public Long bitCount(String key) {
        return redisTemplate.execute((RedisCallback<Long>)redisConnection -> redisConnection.bitCount(key.getBytes()));
    }

    /**
     * 设置bit
     * 
     * @param key
     * @param offset
     * @param value
     * @return
     */
    public Boolean setBit(String key, Long offset, Boolean value) {
        return redisTemplate.execute((RedisCallback<Boolean>)redisConnection -> redisConnection.setBit(key.getBytes(), offset, value));
    }

    /**
     * 获取bit
     * 
     * @param key
     * @param offset
     * @return
     */
    public Boolean getBit(String key, Long offset) {
        return redisTemplate.execute((RedisCallback<Boolean>)redisConnection -> redisConnection.getBit(key.getBytes(), offset));
    }
}
