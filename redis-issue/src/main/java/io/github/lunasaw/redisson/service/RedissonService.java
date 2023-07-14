package io.github.lunasaw.redisson.service;

import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author weidian
 * @date 2023/7/14
 */
@Component
public class RedissonService {

    @Autowired
    private RedissonClient                        redissonClient;

    @Autowired
    private RedissonReactiveClient                redissonReactiveClient;

    @Autowired
    private RedissonRxClient                      redissonRxClient;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

}
