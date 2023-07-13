package io.github.lunasaw.limit.service;

public interface RateLimitService {

    /**
     * 多少秒内最大请求量
     * 
     * @param key 唯一
     * @param maxRequests 限流数
     * @param durationInSeconds 最大请求时间区间
     * @return
     */
    boolean isAllowed(String key, int maxRequests, int durationInSeconds);
}
