package com.eguglielmelli.controllers;

import com.eguglielmelli.models.Account;
import com.eguglielmelli.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
