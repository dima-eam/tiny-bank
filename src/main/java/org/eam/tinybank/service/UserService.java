package org.eam.tinybank.service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateUserRequest;
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
    public ApiResponse create(@NonNull CreateUserRequest request) {
        return invalid(request)
            .orElseGet(() -> create(User.from(request)));
    }

    public ApiResponse deactivate(@NonNull String email) {
        return userDao.deactivate(email)
            .map(u -> ApiResponse.deactivated())
            .orElseGet(() -> ApiResponse.userNotFound(email));
    }

    private ApiResponse create(User user) {
        return userDao.store(user)
            .map(u -> ApiResponse.userExists())
            .orElseGet(ApiResponse::userCreated);
    }

    private static Optional<ApiResponse> invalid(CreateUserRequest request) {
        return request.validEmail() ? Optional.empty() : Optional.of(ApiResponse.error("Invalid email"));
    }

}