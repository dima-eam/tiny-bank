package org.eam.tinybank.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.AllArgsConstructor;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Encapsulates endpoints for account operations, such as create and deposit. User must have an account created
 * beforehand.
 */
@RestController()
@RequestMapping("/api/account")
@AllArgsConstructor
class AccountController implements RestSupport {

    private final AccountService accountService;

    @PutMapping(path = "create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> create(@RequestBody CreateAccountRequest request) {
        return responseFrom(accountService.create(request));
    }

}