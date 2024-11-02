package org.eam.tinybank.api;

import lombok.NonNull;

/**
 * Encapsulates data needed to create an account. Assumed that user profile was created beforehand.
 * @param email
 */
public record CreateAccountRequest(@NonNull String email) {

}
