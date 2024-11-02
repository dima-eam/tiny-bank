package org.eam.tinybank.controller;

import lombok.AllArgsConstructor;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.api.UserResponse;
import org.eam.tinybank.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Encapsulates endpoints for user operations, such as create and deactivate, and performs basic validation.
 */
@RestController("/user")
@AllArgsConstructor
class UserController {

    private final UserService userService;

    @PutMapping("/create")
    ResponseEntity<UserResponse> create(CreateUserRequest request) {
        UserResponse response = userService.create(request);

        return ResponseEntity.status(from(response)).body(response);
    }

    private HttpStatus from(UserResponse response) {
        return response.failed() ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED;
    }


}