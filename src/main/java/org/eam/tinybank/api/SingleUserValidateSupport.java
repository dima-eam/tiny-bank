package org.eam.tinybank.api;

import java.util.Set;

/**
 * More rich interface for entities with only one email to check, such as deposit request.
 */
public interface SingleUserValidateSupport extends UserValidateSupport {

    String email();

    /**
     * {@inheritDoc}
     */
    @Override
    default Set<String> emailsToCheck() {
        return Set.of(email());
    }

}
