package io.zambee.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Optional;

@RequiredArgsConstructor
public enum TransactionType {
    CASH_WITHDRAWAL("Cash Withdrawal"),
    SEPA_DIRECT_DEBIT("SEPA-Direct Debit"),
    SEPA_CREDIT_TRANSFER("SEPA-Credit Transfer"),
    DEBIT_CARD_PAYMENT("Debit Card Payment"),
    SEPA_DIRECT_DEBIT_ELV("SEPA-Direct Debit (ELV)");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Optional<TransactionType> evaluate(String value) {
        return EnumSet.allOf(TransactionType.class).stream().filter(t -> t.getValue().equals(value)).findFirst();
    }

    @JsonCreator
    public static TransactionType fromValue(String value) {
        return evaluate(value).orElse(null);
    }

}
