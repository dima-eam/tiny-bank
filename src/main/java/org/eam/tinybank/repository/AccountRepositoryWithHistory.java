package org.eam.tinybank.repository;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.eam.tinybank.domain.AccountEntity;
import org.eam.tinybank.domain.HistoryEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountRepositoryWithHistory {

    private final AccountRepository repository;
    private final HistoryRepository historyRepository;

    @NonNull
    public Optional<AccountEntity> byEmail(@NonNull String email) {
        return repository.findById(email);
    }

    public void deposit(@NonNull AccountEntity entity, @NonNull BigDecimal amount) {
        repository.save(entity);
        historyRepository.save(HistoryEntity.deposit(entity.getEmail(), amount));
    }

}