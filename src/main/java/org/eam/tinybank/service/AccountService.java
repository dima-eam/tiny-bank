package org.eam.tinybank.service;

import java.math.BigDecimal;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.DepositRequest;
import org.eam.tinybank.dao.AccountDao;
import org.eam.tinybank.dao.UserDao;
import org.springframework.stereotype.Component;

/**
 * Encapsulates validation and conversion logic for account management operations, and calls data access layer. Account
 * operation are only allowed for existing and active users.
 */
@Component
@AllArgsConstructor
public class AccountService {

    UserDao userDao;
    AccountDao accountDao;

    public ApiResponse create(@NonNull CreateAccountRequest request) {
        return processIfActive(request.email(), () -> create(request.email()));
    }

    public ApiResponse deposit(@NonNull DepositRequest request) {
        return processIfActive(request.email(), () -> deposit(request.email(), request.amount()));
    }

    private ApiResponse create(@NonNull String email) {
        return accountDao.create(email)
            .map(a -> ApiResponse.accountExists())
            .orElseGet(ApiResponse::accountCreated);
    }

    private ApiResponse deposit(@NonNull String email, @NonNull BigDecimal amount) {
        return accountDao.deposit(email, amount)
            .map(a -> ApiResponse.deposited())
            .orElseGet(() -> ApiResponse.accountNotFound(email));
    }

    private ApiResponse processIfActive(@NonNull String email, @NonNull Supplier<ApiResponse> responseSupplier) {
        return userDao.retrieve(email)
            .map(u -> u.active() ? responseSupplier.get() : ApiResponse.inactive())
            .orElse(ApiResponse.userNotFound(email));
    }

}