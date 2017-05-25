package io.zambee.controller;

import io.zambee.dto.TransactionDTO;
import io.zambee.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping(value = "/upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void loadFromFile(@RequestParam("file") MultipartFile file) {
        transactionService.processFile(file);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<TransactionDTO> getAll() {
        return transactionService.getAllTransactions();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TransactionDTO findById(@PathVariable("id") UUID id) {
        return transactionService.findById(id);
    }

}
