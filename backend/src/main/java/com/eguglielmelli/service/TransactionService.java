package com.eguglielmelli.service;

import com.eguglielmelli.models.*;
import com.eguglielmelli.repositories.AccountRepository;
import com.eguglielmelli.repositories.CategoryRepository;
import com.eguglielmelli.repositories.PayeeRepository;
import com.eguglielmelli.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PayeeRepository payeeRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,PayeeRepository payeeRepository, CategoryRepository categoryRepository,AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.payeeRepository = payeeRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction createTransaction(Long accountId, Long payeeId, Long categoryId, BigDecimal amount, Date date, String description, TransactionType type) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID must not be null");
        }
        if (payeeId == null) {
            throw new IllegalArgumentException("Payee ID must not be null");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID must not be null");
        }
        // Fetch the Account, Payee, and Category based on their IDs
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Payee payee = payeeRepository.findById(payeeId)
                .orElseThrow(() -> new RuntimeException("Payee not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Create a new Transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setPayee(payee);
        transaction.setCategory(category);
        transaction.setAmount(type == TransactionType.OUTFLOW ? amount.negate() : amount);
        transaction.getAccount().adjustBalanceForTransaction(transaction.getAmount());
        category.adjustBalancesForTransaction(amount,type);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type);

        validateTransaction(transaction);

        return transactionRepository.save(transaction);
    }


    @Transactional
    public Transaction save(Transaction transaction) {
        validateTransaction(transaction);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(Long id,Transaction updatedTransaction) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction with that ID not found"));

        transaction.setAccount(updatedTransaction.getAccount());
        transaction.getAccount().adjustBalanceForTransaction(updatedTransaction.getAmount());
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setCategory(updatedTransaction.getCategory());
        updatedTransaction.getCategory().adjustBalancesForTransaction(updatedTransaction.getAmount(),updatedTransaction.getType());
        transaction.setDate(updatedTransaction.getDate());
        transaction.setDescription(updatedTransaction.getDescription());
        transaction.setPayee(updatedTransaction.getPayee());
        transaction.setType(updatedTransaction.getType());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no transaction with that ID."));

        adjustAccountBalance(transaction.getAccount(),transaction);
        adjustCategoryBalance(transaction.getCategory(),transaction);
        transactionRepository.delete(transaction);
    }

    @Transactional
    public Transaction moveTransactionAccount(Long id, Account account) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction with that ID not found"));

        validateTransactionForMove(transaction,account);

        transaction.setAccount(account);

        return transactionRepository.save(transaction);

    }
    @Transactional
    public Transaction findTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No transaction with that ID."));
        return transaction;
    }

    @Transactional
    public List<Transaction> findTransactionsByCategory(Category category) {
        if(category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return transactionRepository.findByCategory(category);
    }

    @Transactional
    public List<Transaction> findTransactionByPayee(Payee payee) {
        if(payee == null) {
            throw new IllegalArgumentException("Payee cannot be null");
        }
        return transactionRepository.findByPayee(payee);
    }

    @Transactional
    public List<Transaction> findTransactionByDate(Date startDate,Date endDate) {
        if(startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate cannot be null.");
        }
        return transactionRepository.findByDateBetween(startDate,endDate);
    }

    //Private helper methods dealing with validation

    private void validateTransactionForMove(Transaction transaction,Account newAccount) {
        if(newAccount == null) throw new IllegalArgumentException("New account cannot be null.");

    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() == null) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        if (transaction.getAccount() == null) {
            throw new IllegalArgumentException("Transaction must be associated with an account.");
        }

        if (transaction.getPayee() == null) {
            throw new IllegalArgumentException("Transaction must have a payee.");
        }

        if (transaction.getCategory() == null) {
            throw new IllegalArgumentException("Transaction must have a category.");
        }

        if (transaction.getDate() == null) {
            throw new IllegalArgumentException("Transaction date cannot be null.");
        }

        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction description cannot be empty.");
        }

        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Transaction type must be specified.");
        }
    }
    @Transactional
    private void adjustAccountBalance(Account account, Transaction transaction) {
        BigDecimal transactionAmount = transaction.getAmount();

        if (transaction.getType() == TransactionType.INFLOW) {
            account.setBalance(account.getBalance().subtract(transactionAmount));
        } else if (transaction.getType() == TransactionType.OUTFLOW) {
            account.setBalance(account.getBalance().add(transactionAmount));
        }

        accountRepository.save(account);
    }
    @Transactional
    private void adjustCategoryBalance(Category category, Transaction transaction) {
        BigDecimal transactionAmount = transaction.getAmount();

        if (transaction.getType() == TransactionType.INFLOW) {
            category.setSpent(category.getSpent().subtract(transactionAmount));
        } else if (transaction.getType() == TransactionType.OUTFLOW) {
            category.setSpent(category.getSpent().add(transactionAmount));
        }

        category.setAvailable(category.getBudgetedAmount().subtract(category.getSpent()));

        categoryRepository.save(category);
    }
}
