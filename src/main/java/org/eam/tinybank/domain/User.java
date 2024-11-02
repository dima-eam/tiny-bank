package org.eam.tinybank.domain;

import lombok.NonNull;
import org.eam.tinybank.api.CreateUserRequest;

public record User(@NonNull String firstname, @NonNull String lastname, @NonNull String email, @NonNull Status status) {

    /**
     * Creates a user record from given request, and active status.
     */
    public static User from(CreateUserRequest request) {
        return new User(request.firstname(), request.lastname(), request.email(), Status.ACTIVATED);
    }

    public User deactivated() {
        return new User(firstname(), lastname(), email(), Status.DEACTIVATED);
    }

    private enum Status {
        ACTIVATED,
        DEACTIVATED
    }

}