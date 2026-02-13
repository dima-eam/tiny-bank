package org.eam.tinybank.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents a user account, identified by email. Holds current balance and history to maintain atomicity.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class AccountEntity {

    @Id
    @NonNull
    private String email;
    @NonNull
    private BigDecimal balance;

    /**
     * Creates a user account record from the given email, with zero balance.
     */
    public static AccountEntity from(String email) {
        return new AccountEntity(email, BigDecimal.ZERO);
    }

    public AccountEntity deposited(@NonNull BigDecimal amount) {
        return new AccountEntity(email, balance.add(amount));
    }

    public AccountEntity withdrawed(@NonNull BigDecimal amount) {
        return new AccountEntity(email, balance.subtract(amount));
    }

    public boolean canWithdraw(@NonNull BigDecimal amount) {
        return balance.subtract(amount).compareTo(BigDecimal.ZERO) > 0;
    }

}