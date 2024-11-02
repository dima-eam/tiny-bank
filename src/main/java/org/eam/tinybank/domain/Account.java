package org.eam.tinybank.domain;

import java.math.BigDecimal;
import lombok.NonNull;

public record Account(@NonNull String email, @NonNull BigDecimal balance) {

    /**
     * Creates a user record from given request, and active status.
     */
    public static Account from(String email) {
        return new Account(email, BigDecimal.ZERO);
    }

    public Account deposited(@NonNull BigDecimal amount) {
        return new Account(email, balance.add(amount));
    }

}