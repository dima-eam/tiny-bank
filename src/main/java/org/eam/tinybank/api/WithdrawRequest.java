package org.eam.tinybank.api;

import java.math.BigDecimal;
import lombok.NonNull;

/**
 * Encapsulates data to perform an account withdrawal operation. Even though it looks identical to
 * {@link DepositRequest} they belong to different domains.
 */
public record WithdrawRequest(@NonNull String email, @NonNull BigDecimal amount) implements
    SingleUserValidateSupport, AmountValidateSupport {

}