package org.eam.tinybank.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
     * Creates a user account record from given email, with zero balance.
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

    public AccountEntity transferredTo(@NonNull BigDecimal amount, @NonNull String emailTo) {
        return new AccountEntity(email, balance.subtract(amount));
    }

    public AccountEntity receivedFrom(@NonNull BigDecimal amount, @NonNull String emailFrom) {
        return new AccountEntity(email, balance.add(amount));
    }

    /**
     * Auxiliary class holding account history. Stores entries in  SynchronizedList for simplicity, since there is no
     * direct access to its content.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class History {

        private final List<Operation> operations = Collections.synchronizedList(new ArrayList<>());

        private History add(Operation operation) {
            operations.add(operation);

            return this;
        }

        public List<String> asString() {
            return operations.stream()
                .map(Operation::toString)
                .toList();
        }

    }

    /**
     * Auxiliary class representing an account operation, like deposit or withdraw.
     */
    private record Operation(long timestamp, @NonNull String description, @NonNull BigDecimal amount) {

        private static Operation deposit(@NonNull BigDecimal amount) {
            return new Operation(System.currentTimeMillis(), "Deposit", amount);
        }

        private static Operation withdraw(@NonNull BigDecimal amount) {
            return new Operation(System.currentTimeMillis(), "Withdraw", amount);
        }

        public static Operation transferTo(@NonNull BigDecimal amount, @NonNull String emailTo) {
            return new Operation(System.currentTimeMillis(), "Transfer to %s".formatted(emailTo), amount);
        }

        public static Operation receiveFrom(@NonNull BigDecimal amount, @NonNull String emailFrom) {
            return new Operation(System.currentTimeMillis(), "Receive from %s".formatted(emailFrom), amount);
        }

    }

}