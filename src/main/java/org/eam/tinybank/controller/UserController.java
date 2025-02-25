package org.eam.tinybank.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.AllArgsConstructor;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Encapsulates endpoints for user operations, such as create and deactivate. NOTE that there are only two possible HTTP
 * statuses in responses: 200 OK and 400 BAD REQUEST, even though 201 CREATED might be useful.
 */
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
class UserController implements RestSupport {

    private final UserService userService;

    @PostMapping(path = "create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> create(@RequestBody CreateUserRequest request) {
        return responseFrom(userService.create(request));
    }

    @PatchMapping(path = "deactivate", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> deactivate(@RequestParam String email) {
        return responseFrom(userService.deactivate(email));
    }

}