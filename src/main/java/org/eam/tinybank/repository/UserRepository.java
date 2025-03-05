package org.eam.tinybank.repository;

import org.eam.tinybank.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// TODO create alternative solution with QL and compare performance using Docker (both DB and service)
@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {

}