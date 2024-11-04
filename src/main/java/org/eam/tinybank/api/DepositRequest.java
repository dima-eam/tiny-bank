package org.eam.tinybank.api;

import java.math.BigDecimal;
import lombok.NonNull;

/**
 * Encapsulates data to perform an account deposit operation.
 */
public record DepositRequest(@NonNull String email, @NonNull BigDecimal amount)
    implements SingleUserValidateSupport, AmountValidateSupport {

}