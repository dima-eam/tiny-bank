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

    public UserResponse create(CreateUserRequest request) {
        return invalid(request)
            .orElseGet(() -> stored(request));
    }

    private static Optional<UserResponse> invalid(CreateUserRequest request) {
        return request.validEmail() ? Optional.empty() : Optional.of(UserResponse.error("Invalid email"));
    }

    private UserResponse stored(CreateUserRequest request) {
        userDao.store(User.from(request));

        return UserResponse.created();
    }

}