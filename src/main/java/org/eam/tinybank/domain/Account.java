package org.eam.tinybank.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents a user account, identified by email. Holds current balance and history to maintain atomicity.
 */
public record Account(@NonNull String email, @NonNull BigDecimal balance, @NonNull History history) {

    /**
     * Creates a user account record from given email, with zero balance.
     */
    public static Account from(String email) {
        return new Account(email, BigDecimal.ZERO, new History());
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

    public Account transferredTo(@NonNull BigDecimal amount, @NonNull String emailTo) {
        return new Account(email, balance.subtract(amount), history.add(Operation.transferTo(amount, emailTo)));
    }

    public Account receivedFrom(@NonNull BigDecimal amount, @NonNull String emailFrom) {
        return new Account(email, balance.add(amount), history.add(Operation.receiveFrom(amount, emailFrom)));
    }

    /**
     * Auxiliary class holding account history. Stores entries in {@link java.util.Collections.SynchronizedList} for
     * simplicity, since there is no direct access to its content.
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