package org.eam.tinybank.service;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.eam.tinybank.api.AmountValidateSupport;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.DepositRequest;
import org.eam.tinybank.api.TransferRequest;
import org.eam.tinybank.api.UserValidateSupport;
import org.eam.tinybank.api.WithdrawRequest;
import org.eam.tinybank.dao.AccountDao;
import org.eam.tinybank.dao.UserDao;
import org.eam.tinybank.domain.Account;
import org.eam.tinybank.domain.User;
import org.springframework.stereotype.Component;

/**
 * Encapsulates validation and conversion logic for account management operations, and calls data access layer. Account
 * operation are only allowed for existing and active users, so every method has a check, and also amount is checked
 * whether needed. NOTE that email is not validated here.
 */
@Component
@AllArgsConstructor
public class AccountService {

    UserDao userDao;
    AccountDao accountDao;

    public ApiResponse create(@NonNull CreateAccountRequest request) {
        return invalidUser(request)
            .orElseGet(() -> create(request.email()));
    }

    public ApiResponse deposit(@NonNull DepositRequest request) {
        return invalidAmount(request)
            .or(() -> invalidUser(request))
            .orElseGet(() -> deposit(request.email(), request.amount()));
    }

    public ApiResponse withdraw(@NonNull WithdrawRequest request) {
        return invalidAmount(request)
            .or(() -> invalidUser(request))
            .orElseGet(() -> withdraw(request.email(), request.amount()));
    }

    public ApiResponse transfer(@NonNull TransferRequest request) {
        return invalidAmount(request)
            .or(() -> invalidUser(request))
            .orElseGet(() -> transfer(request.emailFrom(), request.emailTo(), request.amount()));
    }

    public ApiResponse balance(@NonNull String email) {
        return invalidUser(email)
            .orElseGet(() -> accountDao.retrieve(email)
                .map(a -> ApiResponse.balance(a.balance()))
                .orElseGet(() -> ApiResponse.accountNotFound(email)));
    }

    public ApiResponse history(@NonNull String email) {
        return invalidUser(email)
            .orElseGet(() -> accountDao.retrieve(email)
                .map(a -> ApiResponse.history(a.history().asString()))
                .orElseGet(() -> ApiResponse.accountNotFound(email)));
    }

    private ApiResponse create(String email) {
        return accountDao.create(email)
            .map(a -> ApiResponse.accountExists())
            .orElseGet(ApiResponse::accountCreated);
    }

    private ApiResponse deposit(String email, BigDecimal amount) {
        return accountDao.deposit(email, amount)
            .map(a -> ApiResponse.deposited(a.balance()))
            .orElseGet(() -> ApiResponse.accountNotFound(email));
    }

    /**
     * Performs an atomic withdrawal operation from a given account. Looks a bit complicated, but covers all invariants:
     * when account does not exist, when account has insufficient funds, and when withdrawal is possible.
     * <p>
     * Side effect is used to capture the fact that account has insufficient funds, because DAO call can only return
     * {@link Account} instance or null.
     */
    private ApiResponse withdraw(String email, BigDecimal amount) {
        Capture capture = new Capture();

        return accountDao.withdraw(email, amount, a -> testAndCapture(amount, a, capture))
            .map(a -> capture.captured()
                .map(ApiResponse::error)
                .orElseGet(() -> ApiResponse.withdrawed(a.balance())))
            .orElseGet(() -> ApiResponse.accountNotFound(email));
    }

    /**
     * Performs an atomic withdrawal operation from a given account, followed by atomic deposit to receiver account, but
     * there is no transactional support. Looks a bit complicated, but covers all invariants: when any account does not
     * exist, when sender account has insufficient funds, and when withdrawal is possible.
     * <p>
     * Side effect is used to capture the fact that sender account has insufficient funds, because DAO call can only
     * return {@link Account} instance or null.
     */
    private ApiResponse transfer(@NonNull String emailFrom, @NonNull String emailTo, @NonNull BigDecimal amount) {
        Capture capture = new Capture();

        return accountDao.retrieve(emailTo) // this only needed to check if receiver account exists
            .map(aTo -> accountDao.transfer(emailFrom, emailTo, amount, a -> testAndCapture(amount, a, capture))
                .map(a -> capture.captured()
                    .map(ApiResponse::error)
                    .orElseGet(() -> ApiResponse.transferred(emailFrom, emailTo)))
                .orElseGet(() -> ApiResponse.accountNotFound(emailFrom)))
            .orElseGet(() -> ApiResponse.accountNotFound(emailTo));
    }

    private static Optional<ApiResponse> invalidAmount(AmountValidateSupport request) {
        return request.validAmount() ? Optional.empty() : Optional.of(ApiResponse.error("Invalid amount"));
    }

    /**
     * Checks all given emails and returns first error found.
     */
    private Optional<ApiResponse> invalidUser(UserValidateSupport request) {
        return request.emailsToCheck()
            .stream()
            .map(this::invalidUser)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    /**
     * Checks user profile by email. If profile not found or inactive, returns corresponding response.
     */
    private Optional<ApiResponse> invalidUser(String email) {
        var user = userDao.retrieve(email);
        var response = user.isEmpty() ? ApiResponse.userNotFound(email)
            : user.filter(User::inactive).map(u -> ApiResponse.inactive()).orElse(null);

        return Optional.ofNullable(response);
    }

    /**
     * Side effect based test of withdrawal condition. Allows capturing of negative result as string message to be
     * returned back to client. NOTE that this is an alternative to throwing exception or having account balance check
     * beforehand (which is not possible to do atomically).
     */
    private static boolean testAndCapture(BigDecimal amount, Account account, Capture capture) {
        var result = account.canWithdraw(amount);
        capture.captured = result ? null : "Insufficient funds";

        return result;
    }

    /**
     * Auxiliary class to be used in closures to capture information not possible to return as result.
     */
    private static class Capture {

        private String captured;

        Optional<String> captured() {
            return Optional.ofNullable(captured);
        }

    }

}