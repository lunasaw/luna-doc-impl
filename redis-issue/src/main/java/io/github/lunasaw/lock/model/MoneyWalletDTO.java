package io.github.lunasaw.lock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyWalletDTO implements Serializable {

    private String     id;
    private String     name;
    private String     email;
    private String     currency;
    private BigDecimal balance;
}
