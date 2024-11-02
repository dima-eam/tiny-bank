package org.eam.tinybank.dao;

import java.util.Optional;
import org.eam.tinybank.domain.User;
import org.springframework.stereotype.Component;

/**
 * Encapsulated user management at storage level. Assumed that primary key for user is email.
 */
@Component
public class UserDao extends InMemoryDao<String, User> {

    public Optional<User> store(User user) {
        return store(user.email(), user);
    }

    public Optional<User> deactivate(String email) {
        return update(email, User::deactivated);
    }

}