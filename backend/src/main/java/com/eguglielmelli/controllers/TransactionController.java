package com.eguglielmelli.controllers;

import com.eguglielmelli.dto.TransferFundsRequest;
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
    @PutMapping("/{id}/update")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        transactionService.update(id,transaction);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.findTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<?> transferFundsBetweenAccounts(@PathVariable Long accountId, @RequestBody TransferFundsRequest transferRequest) {
        try {
            transactionService.transferFundsBetweenAccounts(accountId,transferRequest);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    @GetMapping("/{userId}/transactionsByDate")
//    public


}
