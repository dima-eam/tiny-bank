package org.eam.tinybank.domain;

import java.math.BigDecimal;
import lombok.NonNull;

/**
 * Represents a user account, identified by email. Holds only current balance, and history is updated outside.
 */
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

    public Account withdrawed(@NonNull BigDecimal amount) {
        return new Account(email, balance.subtract(amount));
    }

    public boolean canWithdraw(@NonNull BigDecimal amount) {
        return balance.subtract(amount).compareTo(BigDecimal.ZERO) > 0;
    }

}