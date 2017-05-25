package io.zambee.service;

import io.zambee.domain.Transaction;
import io.zambee.repository.TransactionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void analyze() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:transactions_test01.csv");
        InputStream is = resource.getInputStream();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("Transactions", is);
        transactionService.processFile(mockMultipartFile);
        List<Transaction> all = transactionRepository.findAll();
        assertEquals(50, all.size());
    }
}