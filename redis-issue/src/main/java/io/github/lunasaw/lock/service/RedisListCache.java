package io.github.lunasaw.lock.service;

import java.math.BigDecimal;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.github.lunasaw.lock.model.MoneyWalletDTO;
import io.github.lunasaw.lock.service.locker.DistributedLocker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisListCache {

    private final DistributedLocker distributedLocker;
    private final RedisValueCache   valueCache;

    public RedisListCache(DistributedLocker distributedLocker,
        RedisValueCache valueCache) {
        this.distributedLocker = distributedLocker;
        this.valueCache = valueCache;
    }

    /**
     * first get cached element from the MoneyWalletDTO id field
     * then get the cached element locked.
     * then the locked element add the balance.
     * then the locked element set the balance.
     * then the locked element unlock with automatic unlock in same thread, after the finally block.
     * 
     * @param key
     * @param amount
     */
    public void addBalance(final String key, final BigDecimal amount) {
        MoneyWalletDTO cachedWalletDTO = (MoneyWalletDTO)valueCache.getCachedValue(key);
        distributedLocker.lock(cachedWalletDTO.getId(), 5, 6, () -> {
            cachedWalletDTO.setBalance(cachedWalletDTO.getBalance().add(amount));
            valueCache.cache(cachedWalletDTO.getId(), cachedWalletDTO);
            return null;
        });

    }

    /**
     * first get cached element from the MoneyWalletDTO id field
     * then get the cached element locked.
     * then the locked element subtract the balance.
     * then the locked element set the balance.
     * then the locked element unlock with automatic unlock in same thread, after the finally block.
     * 
     * @param key
     * @param amount
     */
    public void subtractBalance(String key, BigDecimal amount) {
        MoneyWalletDTO cachedWalletDTO = (MoneyWalletDTO)valueCache.getCachedValue(key);
        distributedLocker.lock(cachedWalletDTO.getId(), 5, 6, () -> {
            cachedWalletDTO.setBalance(cachedWalletDTO.getBalance().subtract(amount));
            valueCache.cache(cachedWalletDTO.getId(), cachedWalletDTO);
            return null;
        });
    }
}