package org.eam.tinybank.domain;

import lombok.NonNull;
import org.eam.tinybank.api.CreateUserRequest;

/**
 * Represents a user profile registered in the system.
 */
public record User(@NonNull String firstname, @NonNull String lastname, @NonNull String email, @NonNull Status status) {

    /**
     * Creates a user record from given request, and active status.
     */
    public static User from(@NonNull CreateUserRequest request) {
        return new User(request.firstname(), request.lastname(), request.email(), Status.ACTIVATED);
    }

    /**
     * Creates a <b>new</b> instance with same data but inactive.
     */
    public User deactivated() {
        return new User(firstname(), lastname(), email(), Status.DEACTIVATED);
    }

    public boolean inactive() {
        return status == Status.DEACTIVATED;
    }

    private enum Status {
        ACTIVATED,
        DEACTIVATED
    }

}