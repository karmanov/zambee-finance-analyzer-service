package io.zambee.repository;

import io.zambee.domain.Transaction;
import io.zambee.domain.TransactionCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TransactionRepository extends MongoRepository<Transaction, UUID> {

    Optional<Transaction> findById(UUID id);

    Set<Transaction> findByCategory(TransactionCategory transactionCategory);
}
