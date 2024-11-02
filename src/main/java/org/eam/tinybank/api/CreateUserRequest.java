package org.eam.tinybank.api;

import java.util.regex.Pattern;
import lombok.NonNull;

/**
 * Very basic representation of a user.Provide basic email validation capabilities. NOTE that in real life email
 * validation should be more strict.
 */
public record CreateUserRequest(@NonNull String firstname, @NonNull String lastname, @NonNull String email) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(\\S+)$");

    /**
     * Returns 'true' if email is considered to be a valid email.
     */
    public boolean validEmail() {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Removes email to not be printed.
     */
    @Override
    public String toString() {
        return """
            CreateUserRequest{firstname=$firstname,lastname=$lastname
            """;
    }

}