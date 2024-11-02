package org.eam.tinybank.api;

import java.math.BigDecimal;
import lombok.NonNull;

public record WithdrawRequest(@NonNull String email, @NonNull BigDecimal amount) implements ValidateSupport {

}
