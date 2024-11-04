package org.eam.tinybank.api;

import java.util.Set;

/**
 * Enables checking capabilities for implementing classes, but actual implementation depends on the context.
 */
public interface UserValidateSupport {

    /**
     * Returns emails to check. Usually it's only one, so additional interface exists {@link SingleUserValidateSupport},
     * but in case of money transfer two emails have to be checked.
     */
    Set<String> emailsToCheck();
}
