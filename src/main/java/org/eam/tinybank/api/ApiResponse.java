package org.eam.tinybank.api;

import java.math.BigDecimal;
import java.util.List;
import lombok.NonNull;

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
        return new ApiResponse("User not found: email=%s".formatted(email), Status.FAIL);
    }

    public static ApiResponse accountNotFound(@NonNull String email) {
        return new ApiResponse("Account not found: email=%s".formatted(email), Status.FAIL);
    }

    public static ApiResponse inactive() {
        return new ApiResponse("User is inactive", Status.FAIL);
    }

    public static ApiResponse balance(@NonNull BigDecimal balance) {
        return new ApiResponse("Balance: %s".formatted(balance.toPlainString()), Status.SUCCESS);
    }

    public static ApiResponse error(@NonNull String message) {
        return new ApiResponse(message, Status.FAIL);
    }

    public static ApiResponse history(@NonNull List<String> operations) {
        return new ApiResponse("History: %s".formatted(operations), Status.SUCCESS);
    }

    public boolean failed() {
        return status == Status.FAIL;
    }

    private enum Status {
        SUCCESS,
        FAIL
    }

}