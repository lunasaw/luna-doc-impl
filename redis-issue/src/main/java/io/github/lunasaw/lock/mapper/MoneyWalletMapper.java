package io.github.lunasaw.lock.mapper;

import io.github.lunasaw.lock.model.MoneyWalletDTO;

public class MoneyWalletMapper {

    private MoneyWalletMapper() {}

    public static MoneyWalletDTO cacheObjToMoneyWalletDTO(Object cachedValue) {
        return (MoneyWalletDTO)cachedValue;
    }
}
