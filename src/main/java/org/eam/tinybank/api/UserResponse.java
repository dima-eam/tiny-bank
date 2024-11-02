package org.eam.tinybank.api;

import lombok.NonNull;

/**
 * Encapsulates the result of user endpoint calls. NOTE that it is expected that class instances are created via factory
 * methods, rather that constructor calls.
 */
public record UserResponse(@NonNull String message, @NonNull Status status) {

    public static UserResponse created() {
        return new UserResponse("User was created", Status.SUCCESS);
    }

    public static UserResponse deactivated() {
        return new UserResponse("User was deactivated", Status.SUCCESS);
    }

    public static UserResponse error(String message) {
        return new UserResponse(message, Status.FAIL);
    }

    public boolean failed() {
        return status == Status.FAIL;
    }

    private enum Status {
        SUCCESS,
        FAIL
    }

}