package org.eam.tinybank.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.eam.tinybank.api.AmountValidateSupport;
import org.eam.tinybank.api.ApiResponse;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.DepositRequest;
import org.eam.tinybank.api.TransferRequest;
import org.eam.tinybank.api.UserValidateSupport;
import org.eam.tinybank.api.WithdrawRequest;
import org.eam.tinybank.domain.AccountEntity;
import org.eam.tinybank.domain.UserEntity;
import org.eam.tinybank.repository.AccountRepository;
import org.eam.tinybank.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Encapsulates validation and conversion logic for account management operations, and calls data access layer. Account
 * operation are only allowed for existing and active users, so every method has a check, and also amount is checked
 * whether needed. NOTE that email is not validated here.
 */
@Component
@AllArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    /**
     * Checks if account exists, and create one if it doesn't.
     */
    public ApiResponse create(@NonNull CreateAccountRequest request) {
        return invalidUser(request)
            .orElseGet(() -> create(request.email()));
    }

    @Transactional
    public ApiResponse deposit(@NonNull DepositRequest request) {
        return invalidAmount(request)
            .or(() -> invalidUser(request))
            .orElseGet(() -> updateInTransaction(request.email(),
                                                 a -> Optional.empty(),
                                                 a -> a.deposited(request.amount()),
                                                 a -> ApiResponse.deposited(a.getBalance())));
    }

    @Transactional
    public ApiResponse withdraw(@NonNull WithdrawRequest request) {
        return invalidAmount(request)
            .or(() -> invalidUser(request))
            .orElseGet(() -> updateInTransaction(request.email(),
                                                 a -> a.canWithdraw(request.amount())
                                                     ? Optional.empty()
                                                     : Optional.of(ApiResponse.insufficientFunds(a.getEmail())),
                                                 a -> a.withdrawed(request.amount()),
                                                 a -> ApiResponse.withdrawed(a.getBalance())));
    }

    @Transactional
    public ApiResponse transfer(@NonNull TransferRequest request) {
        return invalidAmount(request)
            .or(() -> invalidUser(request))
            .orElseGet(() -> transfer(request.emailFrom(), request.emailTo(), request.amount()));
    }

    public ApiResponse balance(@NonNull String email) {
        return invalidUser(email)
            .orElseGet(() -> accountRepository.findById(email)
                .map(a -> ApiResponse.balance(a.getBalance()))
                .orElseGet(() -> ApiResponse.accountNotFound(email)));
    }

    public ApiResponse history(@NonNull String email) {
        return ApiResponse.accountExists(); // TODO
//        return invalidUser(email)
//            .orElseGet(() -> accountDao.retrieve(email)
//                .map(a -> ApiResponse.history(a.history().asString()))
//                .orElseGet(() -> ApiResponse.accountNotFound(email)));
    }

    /**
     * The call is idempotent, so no transaction is needed.
     */
    private ApiResponse create(@NonNull String email) {
        if (accountRepository.existsById(email)) {
            return ApiResponse.accountExists();
        } else {
            accountRepository.save(AccountEntity.from(email));
            return ApiResponse.accountCreated();
        }
    }

    private ApiResponse updateInTransaction(String email,
                                            Function<AccountEntity, Optional<ApiResponse>> check,
                                            Function<AccountEntity, AccountEntity> update,
                                            Function<AccountEntity, ApiResponse> response) {
        return accountRepository.findById(email)
            .map(a -> check.apply(a)
                .orElseGet(() -> response.apply(accountRepository.save(update.apply(a)))))
            .orElseGet(() -> ApiResponse.accountNotFound(email));
    }

    /**
     * Covers all invariants: when any account does not exist, when sender account has insufficient funds, and when
     * withdrawal is possible. To avoid deadlocks, locking order is always the same, based on emails comparison for
     * simplicity, so the method does not look very pretty.
     */
    private ApiResponse transfer(@NonNull String emailFrom, @NonNull String emailTo, @NonNull BigDecimal amount) {
        Optional<AccountEntity> from;
        Optional<AccountEntity> to;
        if (emailFrom.compareTo(emailTo) < 0) {
            from = accountRepository.findById(emailFrom);
            to = accountRepository.findById(emailTo);
        } else {
            to = accountRepository.findById(emailTo);
            from = accountRepository.findById(emailFrom);
        }

        return to
            .map(aTo -> from.map(aFrom -> aFrom.canWithdraw(amount)
                    ? transfer(aFrom, aTo, amount)
                    : ApiResponse.insufficientFunds(emailFrom))
                .orElseGet(() -> ApiResponse.accountNotFound(emailFrom)))
            .orElseGet(() -> ApiResponse.accountNotFound(emailTo));
    }

    private ApiResponse transfer(AccountEntity aFrom, AccountEntity aTo, @NonNull BigDecimal amount) {
        accountRepository.save(aFrom.withdrawed(amount));
        accountRepository.save(aTo.deposited(amount));

        return ApiResponse.transferred(aFrom.getEmail(), aTo.getEmail());
    }

    private static Optional<ApiResponse> invalidAmount(AmountValidateSupport request) {
        return request.validAmount() ? Optional.empty() : Optional.of(ApiResponse.invalidAmount(request.amount()));
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
    private Optional<ApiResponse> invalidUser(String email) { // TODO simplify if possible
        var user = userRepository.findById(email);
        var response = user.isEmpty() ? ApiResponse.userNotFound(email)
            : user.filter(UserEntity::inactive).map(u -> ApiResponse.inactive()).orElse(null);

        return Optional.ofNullable(response);
    }

}