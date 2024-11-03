package org.eam.tinybank.api;

import java.math.BigDecimal;
import java.util.Set;
import lombok.NonNull;

/**
 * Encapsulates data required for funds transfer, from one account to another.
 */
public record TransferRequest(@NonNull String emailFrom, @NonNull String emailTo, @NonNull BigDecimal amount) implements
    UserValidateSupport, AmountValidateSupport {

    @Override
    public Set<String> emailsToCheck() {
       return Set.of(emailFrom, emailTo);
    }

}