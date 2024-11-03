package org.eam.tinybank.api;

import java.util.Set;

public interface SingleUserValidateSupport extends UserValidateSupport {

    String email();

    @Override
    default Set<String> emailsToCheck() {
        return Set.of(email());
    }

}
