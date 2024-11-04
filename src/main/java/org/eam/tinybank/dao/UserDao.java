package org.eam.tinybank.dao;

import java.util.Optional;
import lombok.NonNull;
import org.eam.tinybank.domain.User;
import org.springframework.stereotype.Component;

/**
 * Encapsulated user management at storage level. Assumed that primary key for user is email. Methods are very
 * straight-forward, thus no Javadocs.
 */
@Component
public class UserDao extends InMemoryDao<String, User> {

    public Optional<User> store(@NonNull User user) {
        return stored(user.email(), user);
    }

    public Optional<User> deactivate(@NonNull String email) {
        return updated(email, User::deactivated);
    }

    public Optional<User> retrieve(@NonNull String email) {
        return retrieved(email);
    }

}