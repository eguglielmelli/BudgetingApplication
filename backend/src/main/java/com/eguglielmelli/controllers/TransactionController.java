package com.eguglielmelli.controllers;

import com.eguglielmelli.models.Transaction;
import com.eguglielmelli.service.TransactionService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(
                transaction.getAccount().getAccountId(), transaction.getPayee().getPayeeId(), transaction.getCategory().getCategoryId(),
                transaction.getAmount(), transaction.getDate(), transaction.getDescription(),
                transaction.getType());
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

}
