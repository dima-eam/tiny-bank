package org.eam.tinybank.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;

/**
 * Represents a user account, identified by email. Holds current balance and history to maintain atomicity.
 */
public record Account(@NonNull String email, @NonNull BigDecimal balance, @NonNull History history) {

    /**
     * Creates a user account record from given email, with zero balance.
     */
    public static Account from(String email) {
        return new Account(email, BigDecimal.ZERO, History.empty());
    }

    public Account deposited(@NonNull BigDecimal amount) {
        return new Account(email, balance.add(amount), history.add(Operation.deposit(amount)));
    }

    public Account withdrawed(@NonNull BigDecimal amount) {
        return new Account(email, balance.subtract(amount), history.add(Operation.withdraw(amount)));
    }

    public boolean canWithdraw(@NonNull BigDecimal amount) {
        return balance.subtract(amount).compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Auxiliary class holding account history. Stores entries in {@link java.util.Collections.SynchronizedList} for
     * simplicity.
     */
    public record History(@NonNull List<Operation> operations) {

        static History empty() {
            return new History(Collections.synchronizedList(new ArrayList<>()));
        }

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

    record Operation(long timestamp, @NonNull String description, @NonNull BigDecimal amount) {

        static Operation deposit(@NonNull BigDecimal amount) {
            return new Operation(System.currentTimeMillis(), "Deposit", amount);
        }

        static Operation withdraw(@NonNull BigDecimal amount) {
            return new Operation(System.currentTimeMillis(), "Withdraw", amount);
        }

    }

}