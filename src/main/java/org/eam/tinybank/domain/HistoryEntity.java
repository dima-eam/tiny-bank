package org.eam.tinybank.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents account history entry, as details of any account operation.
 * TODO transaction rollback test if an exception happens during history?
 * TODO evolve into history microservice with Kafka
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class HistoryEntity { // TODO compare performance with and without index

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // TODO use seq
    private Long id;
    @NonNull
    private String email;
    @NonNull
    private Long timestamp;
    @NonNull
    private String description;
    @NonNull
    private OperationType type;
    @NonNull
    private BigDecimal amount;


    public static HistoryEntity deposit(@NonNull String email, @NonNull BigDecimal amount) {
        return new HistoryEntity(null, email, System.currentTimeMillis(),
                                 "Deposit: %s".formatted(amount), OperationType.DEPOSIT, amount);
    }

    public static HistoryEntity withdraw(@NonNull String email, @NonNull BigDecimal amount) {
        return new HistoryEntity(null, email, System.currentTimeMillis(),
                                 "Withdraw: %s".formatted(amount), OperationType.WITHDRAW, amount);
    }

    public static HistoryEntity transferTo(@NonNull String email, @NonNull String emailTo, @NonNull BigDecimal amount) {
        return new HistoryEntity(null, email, System.currentTimeMillis(),
                                 "Transfer to %s".formatted(emailTo), OperationType.TRANSFER_TO, amount);
    }

    public static HistoryEntity receiveFrom(@NonNull String email,
                                            @NonNull String emailFrom,
                                            @NonNull BigDecimal amount) {
        return new HistoryEntity(null, email, System.currentTimeMillis(),
                                 "Receive from %s".formatted(emailFrom), OperationType.RECEIVE_FROM, amount);
    }

    public String asString() {
        return "description=%s, amount=%s".formatted(description, amount);
    }

}