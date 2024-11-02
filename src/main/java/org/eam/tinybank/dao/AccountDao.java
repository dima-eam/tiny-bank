package org.eam.tinybank.dao;

import java.util.Optional;
import lombok.NonNull;
import org.eam.tinybank.domain.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDao extends InMemoryDao<String, Account> {

    public Optional<Account> create(@NonNull String email) {
        return stored(email, Account.from(email));
    }

}
