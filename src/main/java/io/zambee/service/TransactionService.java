package io.zambee.service;

import com.google.common.collect.Sets;
import io.zambee.converter.ConverterService;
import io.zambee.domain.Transaction;
import io.zambee.domain.TransactionCategory;
import io.zambee.domain.TransactionType;
import io.zambee.dto.TransactionDTO;
import io.zambee.exceptions.TransactionNotFoundException;
import io.zambee.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final ConverterService converterService;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");

    private Set<String> foodVocabulary = Sets.newHashSet("REWE", "EDEKA");
    private Set<String> creditVocabulary = Sets.newHashSet("TARGOBANK");
    private Set<String> amazonVocabulary = Sets.newHashSet("AMAZON EU S.A R.L.");
    private Set<String> rdfVocabulary = Sets.newHashSet("Rundfunk");
    private Set<String> householdChemicalsVocabulary = Sets.newHashSet("DIRK ROSSMANN");
    private Set<String> internetVocabulary = Sets.newHashSet("Vodafone KabelDeutschland");
    private Set<String> cashVocabulary = Sets.newHashSet("COMMERZBANK");
    private Set<String> transportVocabulary = Sets.newHashSet("BVG");
    private Set<String> rentVocabulary = Sets.newHashSet("BGW GmbH & Co. KG");
    private Set<String> paypalVocabulary = Sets.newHashSet("PayPal Europe S.a.r.l");


    public Set<TransactionDTO> getAllTransactions() {
        log.debug("Fetching all transactions");
        return transactionRepository.findAll()
                                    .stream()
                                    .map(this::convert)
                                    .collect(toSet());
    }

    public TransactionDTO findById(UUID id) {
        return convert(transactionRepository.findById(id)
                                            .orElseThrow(() -> new TransactionNotFoundException(id.toString())));
    }

    public void processFile(MultipartFile file) {
        Set<Transaction> transactions = new HashSet<>();
        String line;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length == 18 && !values[0].equals("Booking date")) {
                    log.info("Processing line {}", line);
                    transactions.add(buildTransaction(values));
                }
            }
        } catch (IOException e) {
            log.error("Error occurred. Reason: {} ", e.getMessage());
        }

//        transactions.forEach(System.out::println);
//        log.info("Processed {} transactions", transactions.size());

//        Set<Transaction> filtered = transactions.stream().filter(t -> nonNull(t.getAmount())).collect(toSet());
//        log.info("Count of filtered transactions: {}", filtered.size());

//        BigDecimal allTransactionsSum = calculateSum(filtered);

//        log.info("Total spend: {}", allTransactionsSum);

//        Set<String> uniqueRecipients = filtered.stream().map(Transaction::getBeneficiary).collect(toSet());
//        uniqueRecipients.forEach(System.out::println);
        calculateCategory(transactions);
//        Set<Transaction> foodExpenses = filtered.stream().filter(t -> TransactionCategory.FOOD.equals(t.getCategory())).collect(toSet());
//        BigDecimal foodExpensesSum = calculateSum(foodExpenses);
//        System.out.println("===> " + foodExpensesSum);

//        Set<Transaction> withoutCategory = filtered.stream().filter(t -> Objects.isNull(t.getCategory())).collect(toSet());
//        withoutCategory.forEach(System.out::println);
        transactionRepository.save(transactions);

    }

    private BigDecimal calculateSum(Set<Transaction> transactions) {
        return transactions.stream()
                           .map(Transaction::getAmount)
                           .reduce(BigDecimal::add)
                           .get();
    }

    private void calculateCategory(Set<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            String beneficiary = transaction.getBeneficiary();
            boolean isFood = checkCategories(foodVocabulary, beneficiary);
            boolean isCredit = checkCategories(creditVocabulary, beneficiary);
            boolean isAmazon = checkCategories(amazonVocabulary, beneficiary);
            boolean isRdf = checkCategories(rdfVocabulary, beneficiary);
            boolean isChemicals = checkCategories(householdChemicalsVocabulary, beneficiary);
            boolean isInternet = checkCategories(internetVocabulary, beneficiary);
            boolean isCash = checkCategories(cashVocabulary, beneficiary);
            boolean isTransport = checkCategories(transportVocabulary, beneficiary);
            boolean isRent = checkCategories(rentVocabulary, beneficiary);
            boolean isPaypal = checkCategories(paypalVocabulary, beneficiary);


            if (isFood) {
                transaction.setCategory(TransactionCategory.FOOD);
                continue;
            }

            if (isCredit) {
                transaction.setCategory(TransactionCategory.CREDIT);
                continue;
            }

            if (isAmazon) {
                transaction.setCategory(TransactionCategory.AMAZON);
                continue;
            }

            if (isRdf) {
                transaction.setCategory(TransactionCategory.RDF);
                continue;
            }

            if (isChemicals) {
                transaction.setCategory(TransactionCategory.HOUSEHOLD_CHEMICALS);
                continue;
            }

            if (isInternet) {
                transaction.setCategory(TransactionCategory.INTERNET);
                continue;
            }

            if (isCash) {
                transaction.setCategory(TransactionCategory.CASH);
                continue;
            }

            if (isTransport) {
                transaction.setCategory(TransactionCategory.TRANSPORT);
                continue;
            }

            if (isRent) {
                transaction.setCategory(TransactionCategory.RENT);
                continue;
            }

            if (isPaypal) {
                transaction.setCategory(TransactionCategory.PAYPAL);
                continue;
            }

            transaction.setCategory(TransactionCategory.OTHER);
        }
    }

    private boolean checkCategories(Set<String> vocabulary, String beneficiary) {
        return vocabulary.stream().anyMatch(beneficiary::contains);
    }

    private BigDecimal buildAmount(String value) {
//        log.info("Converting amount value {}", value);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        value = value.replace("-", "");
        value = value.replaceAll(",", "");
        return new BigDecimal(value);
    }

    private TransactionType getTTransactionType(String value) {
        value = value.replaceAll("\"", "");
        return TransactionType.fromValue(value);
    }

    private String getName(String[] values) {
        return values[3].isEmpty() ? values[4] : values[3];
    }

    private Transaction buildTransaction(String[] values) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
//        transaction.setCategory();
        transaction.setType(getTTransactionType(values[2]));
        transaction.setBeneficiary(getName(values));
        transaction.setAmount(buildAmount(values[15]));
        transaction.setValueDate(formatter.parseDateTime(values[0]));
        return transaction;
    }

    private TransactionDTO convert(Transaction transaction) {
        return converterService.convert(transaction, TransactionDTO.class);
    }
}
