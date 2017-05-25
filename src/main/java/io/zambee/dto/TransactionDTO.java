package io.zambee.dto;

import io.zambee.domain.TransactionCategory;
import io.zambee.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private UUID id;

    private TransactionType type;

    private DateTime valueDate;

    private String beneficiary;

    private BigDecimal amount;

    private TransactionCategory category;
}
