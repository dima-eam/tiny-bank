package org.eam.tinybank.api;

import java.math.BigDecimal;

public interface ValidateSupport {

    BigDecimal amount();

    default boolean validAmount() {
        return amount().compareTo(BigDecimal.ZERO) > 0;
    }

}
