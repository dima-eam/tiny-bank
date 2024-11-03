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

    public Optional<Account> transfer(@NonNull String emailFrom, @NonNull String emailTo, @NonNull BigDecimal amount) {
        return Optional.empty();
    }

    public Optional<Account> retrieve(@NonNull String email) {
        return retrieved(email);
    }

}