package org.eam.tinybank.api;

import lombok.NonNull;

/**
 * Encapsulates the result of user endpoint calls. NOTE that it is expected that class instances are created via factory
 * methods, rather that constructor calls.
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

    public static ApiResponse notFound() {
        return new ApiResponse("User not found", Status.FAIL);
    }

    public static ApiResponse inactive() {
        return new ApiResponse("User is inactive", Status.FAIL);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(message, Status.FAIL);
    }

    public boolean failed() {
        return status == Status.FAIL;
    }

    private enum Status {
        SUCCESS,
        FAIL
    }

}