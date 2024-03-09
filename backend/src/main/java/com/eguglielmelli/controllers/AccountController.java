package com.eguglielmelli.controllers;

import com.eguglielmelli.models.Account;
import com.eguglielmelli.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Account> createAccount(@RequestBody Account account, @PathVariable Long userId) {
        Account createdAccount = accountService.createAccount(account,userId);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account>  updateAccountName(@PathVariable Long accountId, @RequestBody Map<String,String> newName) {
        String updatedName = newName.get("updatedName");
        Account account = accountService.updateAccountName(accountId,updatedName);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId) {
        Account account = accountService.findAccountById(accountId);
        return ResponseEntity.ok(account);
    }
}
