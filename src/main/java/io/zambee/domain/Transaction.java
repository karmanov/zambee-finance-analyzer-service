package io.zambee.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private UUID id;

    private TransactionType type;

    private DateTime valueDate;

    private String beneficiary;

    private BigDecimal amount;

    private TransactionCategory category;
}
