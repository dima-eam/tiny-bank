package org.eam.tinybank.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.AllArgsConstructor;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.api.UserResponse;
import org.eam.tinybank.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Encapsulates endpoints for user operations, such as create and deactivate, and performs basic validation.
 */
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
class UserController {

    private final UserService userService;

    @PutMapping(path = "create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        UserResponse response = userService.create(request);

        return ResponseEntity.status(from(response)).body(response);
    }

    @PostMapping(path = "deactivate", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<UserResponse> deactivate(@RequestParam String email) {
        UserResponse response = userService.deactivate(email);

        return ResponseEntity.status(from(response)).body(response);
    }

    private HttpStatus from(UserResponse response) {
        return response.failed() ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
    }


}