package org.eam.tinybank.api;

import java.math.BigDecimal;

/**
 * Provides amount checks for implementing classes.
 */
public interface AmountValidateSupport {

    BigDecimal amount();

    default boolean validAmount() {
        return amount().compareTo(BigDecimal.ZERO) > 0;
    }

}