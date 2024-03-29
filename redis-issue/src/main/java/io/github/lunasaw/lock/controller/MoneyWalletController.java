package io.github.lunasaw.lock.controller;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import io.github.lunasaw.lock.mapper.MoneyWalletMapper;
import io.github.lunasaw.lock.model.MoneyWalletDTO;
import io.github.lunasaw.lock.service.RedisListCache;
import io.github.lunasaw.lock.service.RedisValueCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/money-wallet")
public class MoneyWalletController {

    private final RedisValueCache valueCache;
    private final RedisListCache  listCache;

    @PostMapping
    public void cacheWallet(@RequestBody final MoneyWalletDTO dto) {
        dto.setId(Objects.isNull(dto.getId()) ? UUID.randomUUID().toString() : dto.getId());
        valueCache.cache(dto.getId(), dto);
    }

    @GetMapping("/{id}")
    public MoneyWalletDTO getWallet(@PathVariable final String id) {
        return MoneyWalletMapper.cacheObjToMoneyWalletDTO(valueCache.getCachedValue(id));
    }

    @DeleteMapping("/{id}")
    public void deleteWallet(@PathVariable final String id) {
        valueCache.deleteCachedValue(id);
    }

    @PostMapping("/list/add-balance/{key}/{amount}")
    public void addBalance(@PathVariable final String key, @PathVariable final BigDecimal amount) {
        listCache.addBalance(key, amount);
    }

    @PostMapping("/list/subtract-balance/{key}/{amount}")
    public void subtractBalance(@PathVariable final String key, @PathVariable final BigDecimal amount) {
        listCache.subtractBalance(key, amount);
    }

}
