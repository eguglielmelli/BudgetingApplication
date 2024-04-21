package com.eguglielmelli.service;

import com.eguglielmelli.models.Account;
import com.eguglielmelli.models.Budget;
import com.eguglielmelli.models.Transaction;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.AccountRepository;
import com.eguglielmelli.repositories.BudgetRepository;
import com.eguglielmelli.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private final BudgetRepository budgetRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository,UserRepository userRepository,BudgetRepository budgetRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public Account createAccount(Account account,Long userId) {
        Optional<Account> foundAccount = accountRepository.findByAccountName(account.getAccountName());
        if(foundAccount.isPresent()) {
            throw new IllegalArgumentException("There is already an account with that name.");
        }
        User findUser = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("No user."));
        Budget findBudget = budgetRepository.findById(account.getBudget().getBudgetId()).orElseThrow(() -> new IllegalArgumentException("No account budget found."));
        validateAccount(account);
        account.setAccountType(account.getAccountType().toLowerCase(Locale.ROOT));
        account.setUser(findUser);
        account.setBudget(findBudget);
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        account.getBudget().adjustReadyToAssign(account.getBalance());
        return accountRepository.save(account);
    }
    @Transactional
    public Account updateAccountName(Long id,String newName) {
        Optional<Account> foundAccount = accountRepository.findByAccountId(id);
        if(!foundAccount.isPresent()) {
            throw new IllegalArgumentException("There is no account with that ID.");
        }
        Account account = foundAccount.get();
        account.setAccountName(newName);
        return accountRepository.save(account);

    }
    @Transactional
    public Account findAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No transaction with that ID."));
    }

    @Transactional
    public void deleteAccount(@PathVariable Long accountId) {
        Account account = accountRepository.findByAccountId(accountId).orElseThrow(() -> new RuntimeException("Account with that ID not found."));
        accountRepository.delete(account);
    }

    private void validateAccount(Account account) {
        if (account.getAccountName() == null || account.getAccountName().trim().isEmpty()) {
            throw new IllegalArgumentException("Account name cannot be null or empty.");
        }
        if (account.getAccountType() == null || account.getAccountType().trim().isEmpty()) {
            throw new IllegalArgumentException("Account type cannot be null or empty.");
        }
    }
}
