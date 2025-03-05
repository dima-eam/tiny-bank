package org.eam.tinybank.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import lombok.NonNull;
import org.eam.tinybank.domain.AccountEntity;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, String> {

    /**
     * Enables locking on the row being updated to exclude lost updates.
     * <p>
     * NOTE that this method must be called in the same transaction with update call.
     */
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE) //TODO test with two instances in Docker, compare performance to optimistic
    @NonNull
    Optional<AccountEntity> findById(@NonNull String email);

}