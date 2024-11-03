package org.eam.tinybank.dao;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.NonNull;
import org.eam.tinybank.domain.Account;
import org.springframework.stereotype.Component;

/**
 * Encapsulated account management and fund operations at storage level. Assumed that primary key for user is email.
 */
@Component
public class AccountDao extends InMemoryDao<String, Account> {

    public Optional<Account> create(@NonNull String email) {
        return stored(email, Account.from(email));
    }

    public Optional<Account> deposit(@NonNull String email, @NonNull BigDecimal amount) {
        return updated(email, account -> account.deposited(amount));
    }

    /**
     * Reduce account balance if given predicate returns 'true', otherwise account balance remains same. There is no way
     * to report back if predicate passed, so capturing must be done outside.
     */
    public Optional<Account> withdraw(@NonNull String email, @NonNull BigDecimal amount,
        Predicate<Account> canWithdraw) {
        return updated(email, a -> canWithdraw.test(a) ? a.withdrawed(amount) : a);
    }

    /**
     * Performs withdraw first, so any subsequent calls to sender account will see that change, and if no errors
     * performs deposit to the receiver account from an argument. No synchronisation is required for a receiver, because
     * deposit operation has no conditions.
     */
    public Optional<Account> transfer(@NonNull String emailFrom, @NonNull Account accountTo, @NonNull BigDecimal amount,
        Predicate<Account> canWithdraw) {
        return updated(emailFrom, a -> canWithdraw.test(a) ? a.transferredTo(amount, accountTo.email()) : a)
            .flatMap(a -> stored(accountTo.email(), accountTo.receivedFrom(amount, emailFrom)));
    }

    public Optional<Account> retrieve(@NonNull String email) {
        return retrieved(email);
    }

}