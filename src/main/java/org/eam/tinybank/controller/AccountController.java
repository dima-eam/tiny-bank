package org.eam.tinybank.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.AllArgsConstructor;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.DepositRequest;
import org.eam.tinybank.api.TransferRequest;
import org.eam.tinybank.api.WithdrawRequest;
import org.eam.tinybank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Encapsulates endpoints for account operations, such as create and deposit. User must have a user profile created
 * beforehand. NOTE that there are only two possible HTTP statuses in responses: 200 OK and 400 BAD REQUEST, even though
 * 201 CREATED might be useful.
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

    @PostMapping(path = "deposit", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> deposit(@RequestBody DepositRequest request) {
        return responseFrom(accountService.deposit(request));
    }

    @PostMapping(path = "withdraw", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> deposit(@RequestBody WithdrawRequest request) {
        return responseFrom(accountService.withdraw(request));
    }

    @PostMapping(path = "transfer", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> transfer(@RequestBody TransferRequest request) {
        return responseFrom(accountService.transfer(request));
    }

    @GetMapping(path = "balance", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> balance(@RequestParam String email) {
        return responseFrom(accountService.balance(email));
    }

    @GetMapping(path = "history", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> history(@RequestParam String email) {
        return responseFrom(accountService.history(email));
    }

}