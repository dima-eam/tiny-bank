package org.eam.tinybank.repository;

import java.util.List;
import lombok.NonNull;
import org.eam.tinybank.domain.HistoryEntity;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<HistoryEntity, Long> {

    List<HistoryEntity> findAllByEmail(@NonNull String email);

}