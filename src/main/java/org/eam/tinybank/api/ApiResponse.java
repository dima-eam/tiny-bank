package org.eam.tinybank.api;

import java.math.BigDecimal;
import java.util.List;
import lombok.NonNull;
import org.eam.tinybank.domain.HistoryEntity;
import org.springframework.http.HttpStatus;

/**
 * Encapsulates the result of endpoint calls. NOTE that it is expected that class instances are created via factory
 * methods, rather that constructor calls. Right now all possible responses are defined here, but later may be divided
 * into domain related subclasses, e.g. {@code UserApiResponse}.
 */
public record ApiResponse(@NonNull String message, @NonNull Status status) {

    public static ApiResponse userCreated() {
        return new ApiResponse("User was created", Status.SUCCESS);
    }

    public static ApiResponse accountCreated() {
        return new ApiResponse("Account was created", Status.SUCCESS);
    }

    public static ApiResponse userExists() {
        return new ApiResponse("User exists", Status.SUCCESS);
    }

    public static ApiResponse accountExists() {
        return new ApiResponse("Account exists", Status.SUCCESS);
    }

    public static ApiResponse deactivated() {
        return new ApiResponse("User was deactivated", Status.SUCCESS);
    }

    public static ApiResponse deposited(@NonNull BigDecimal balance) {
        return new ApiResponse("Account was deposited: balance=%s".formatted(balance.toPlainString()), Status.SUCCESS);
    }

    public static ApiResponse withdrawed(@NonNull BigDecimal balance) {
        return new ApiResponse("Account was withdrawed: balance=%s".formatted(balance.toPlainString()), Status.SUCCESS);
    }

    public static ApiResponse transferred(@NonNull String emailFrom, @NonNull String emailTo) {
        return new ApiResponse("Funds transferred: from=%s, to=%s".formatted(emailFrom, emailTo), Status.SUCCESS);
    }

    public static ApiResponse userNotFound(@NonNull String email) {
        return new ApiResponse("User not found: email=%s".formatted(email), Status.FAILED);
    }

    public static ApiResponse accountNotFound(@NonNull String email) {
        return new ApiResponse("Account not found: email=%s".formatted(email), Status.FAILED);
    }

    public static ApiResponse inactive() {
        return new ApiResponse("User is inactive", Status.FAILED);
    }

    public static ApiResponse balance(@NonNull BigDecimal balance) {
        return new ApiResponse("Balance: %s".formatted(balance.toPlainString()), Status.SUCCESS);
    }

    public static ApiResponse invalidEmail(@NonNull String email) {
        return new ApiResponse("Invalid email: %s".formatted(email), Status.FAILED);
    }

    public static ApiResponse invalidAmount(@NonNull BigDecimal amount) {
        return new ApiResponse("Invalid amount: %s".formatted(amount), Status.FAILED);
    }

    public static ApiResponse insufficientFunds(@NonNull String email) {
        return new ApiResponse("Insufficient funds: %s".formatted(email), Status.FAILED);
    }

    public static ApiResponse history(List<HistoryEntity> operations) {
        return new ApiResponse("History: %s".formatted(operations.stream().map(HistoryEntity::asString).toList()),
                               Status.SUCCESS);
    }

    public static ApiResponse error(@NonNull Throwable exception) {
        return new ApiResponse(exception.getMessage(), Status.ERROR);
    }

    /**
     * Evaluates HTTP status code based on API response created in services.
     */
    public HttpStatus statusCode() {
        return switch (status) {
            case SUCCESS -> HttpStatus.OK;
            case ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case FAILED -> HttpStatus.BAD_REQUEST;
        };
    }

    private enum Status {
        /**
         * Request processed successfully
         */
        SUCCESS,
        /**
         * Failed to process due to wrong request parameters
         */
        FAILED,
        /**
         * Failed due to exception/unexpected error
         */
        ERROR
    }

}