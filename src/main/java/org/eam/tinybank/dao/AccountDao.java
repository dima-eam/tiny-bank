package org.eam.tinybank.dao;

import io.micrometer.observation.ObservationFilter;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.NonNull;
import org.eam.tinybank.domain.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDao extends InMemoryDao<String, Account> {

    public Optional<Account> create(@NonNull String email) {
        return stored(email, Account.from(email));
    }

    public Optional<Account> deposit(@NonNull String email, @NonNull BigDecimal amount) {
        return updated(email, account -> account.deposited(amount));
    }

    public Optional<Account> withdraw(@NonNull String email, @NonNull BigDecimal amount) {
        return updated(email, account -> account.withdrawed(amount));
    }

}
