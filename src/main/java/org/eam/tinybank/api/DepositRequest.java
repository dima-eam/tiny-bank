package org.eam.tinybank.api;

import java.math.BigDecimal;
import lombok.NonNull;

public record DepositRequest(@NonNull String email, BigDecimal amount) {

}
