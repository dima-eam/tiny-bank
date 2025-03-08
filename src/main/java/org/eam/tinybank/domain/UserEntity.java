package org.eam.tinybank.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.eam.tinybank.api.CreateUserRequest;

/**
 * Represents a user profile registered in the system.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class UserEntity {

    @Id
    @NonNull
    private String email;
    @NonNull
    private String firstname;
    @NonNull
    private String lastname;
    @Enumerated(EnumType.STRING)
    @NonNull
    private Status status;

    /**
     * Creates a user record from given request, and active status.
     */
    public static UserEntity from(@NonNull CreateUserRequest request) {
        return new UserEntity(request.email(), request.firstname(), request.lastname(), Status.ACTIVATED);
    }

    /**
     * Creates a <b>new</b> instance with same data but inactive.
     */
    public UserEntity deactivated() {
        return new UserEntity(firstname, lastname, email, Status.DEACTIVATED);
    }

    public boolean inactive() {
        return status == Status.DEACTIVATED;
    }

    public enum Status {
        ACTIVATED, DEACTIVATED
    }

}