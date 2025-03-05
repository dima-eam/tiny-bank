package org.eam.tinybank.service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.domain.UserEntity;
import org.eam.tinybank.repository.UserRepository;
import org.springframework.stereotype.Component;

/**
 * Encapsulates validation and conversion logic for user management operations, and calls data access layer.
 */
@Component
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Checks if request is valid, and stores new user record. If user email exists, returns a specific message.
     */
    public ApiResponse create(@NonNull CreateUserRequest request) {
        return invalid(request)
            .orElseGet(() -> create(UserEntity.from(request)));
    }

    public ApiResponse deactivate(@NonNull String email) { // TODO transactional
        return userRepository.findById(email)
            .map(u -> userRepository.save(u.deactivated()))
            .map(u -> ApiResponse.deactivated())
            .orElseGet(() -> ApiResponse.userNotFound(email));
    }

    /**
     * The call is idempotent, so no transaction is needed.
     */
    private ApiResponse create(UserEntity userEntity) {
        if (userRepository.existsById(userEntity.getEmail())) {
            return ApiResponse.userExists();
        } else {
            userRepository.save(userEntity);
            return ApiResponse.userCreated();
        }
    }

    private static Optional<ApiResponse> invalid(CreateUserRequest request) {
        return request.validEmail() ? Optional.empty() : Optional.of(ApiResponse.invalidEmail(request.email()));
    }

}