package org.eam.tinybank.dao;

import org.eam.tinybank.domain.User;
import org.springframework.stereotype.Component;

/**
 * Encapsulated user management on storage level. Assumed that primary key for user is email.
 */
@Component
public class UserDao extends InMemoryDao<String, User>{

   public void store(User user) {
        store(user.email(), user);
    }

   public void deactivate(User user) {
        update(user.email(), user.deactivated());
    }

}