package io.zambee.converter;

import io.zambee.domain.Transaction;
import io.zambee.dto.TransactionDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts {@link io.zambee.domain.Transaction} to {@link io.zambee.dto.TransactionDTO}
 */
@Component
public class TransactionDomainToDTOConverter implements Converter<Transaction, TransactionDTO> {

    @Override
    public TransactionDTO convert(Transaction source) {
        if (source == null) {
            throw new IllegalArgumentException("Could not convert null to TransactionDTO");
        }
        TransactionDTO target = new TransactionDTO();
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
