package org.eam.tinybank.service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.api.UserResponse;
import org.eam.tinybank.dao.UserDao;
import org.eam.tinybank.domain.User;
import org.springframework.stereotype.Component;

/**
 * Encapsulates validation and conversion logic for user management operations, and calls data access layer.
 */
@Component
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;

    /**
     * Checks if request is valid, and stores new user record. If user email exists, returns a specific message.
     */
    public UserResponse create(CreateUserRequest request) {
        return invalid(request)
            .orElseGet(() -> stored(request));
    }

    public UserResponse deactivate(String email) {
        return userDao.deactivate(email)
            .map(user -> UserResponse.deactivated())
            .orElseGet(UserResponse::notFound);
    }

    private static Optional<UserResponse> invalid(CreateUserRequest request) {
        return request.validEmail() ? Optional.empty() : Optional.of(UserResponse.error("Invalid email"));
    }

    private UserResponse stored(CreateUserRequest request) {
        return userDao.store(User.from(request))
            .map(user -> UserResponse.exists())
            .orElseGet(UserResponse::created);
    }

}