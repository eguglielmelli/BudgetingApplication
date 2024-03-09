package com.eguglielmelli.service;

import com.eguglielmelli.dto.TransferFundsRequest;
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
        // Fetch the Account, Payee, and Category based on their IDs
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Payee payee = null;
        if(payeeId != null) {
            payee = payeeRepository.findById(payeeId)
                    .orElseThrow(() -> new RuntimeException("Payee not found"));
        }

        Category category = null;
        if(categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }


        // Create a new Transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setPayee(payee);
        transaction.setCategory(category);
        transaction.setAction(TransactionAction.CREATE);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type);
        validateTransaction(transaction);
        transaction.getAccount().adjustBalanceForTransaction(transaction);
        if(category != null) {
            category.adjustBalancesForTransaction(transaction);
        }


        return saveTransaction(transaction);
    }


    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        validateTransaction(transaction);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(Long id,Transaction updatedTransaction) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with that ID not found"));

        transaction.setAccount(updatedTransaction.getAccount());
        updatedTransaction.setAction(TransactionAction.UPDATE);
        transaction.setAction(TransactionAction.UPDATE);
        transaction.getAccount().adjustBalanceForTransaction(updatedTransaction);
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setCategory(updatedTransaction.getCategory());
        transaction.setDate(updatedTransaction.getDate());
        transaction.setDescription(updatedTransaction.getDescription());
        transaction.setPayee(updatedTransaction.getPayee());
        transaction.setType(updatedTransaction.getType());
        transaction.getCategory().adjustBalancesForTransaction(transaction);

        return saveTransaction(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no transaction with that ID."));
        transaction.setAction(TransactionAction.DELETE);
        transaction.getAccount().adjustBalanceForTransaction(transaction);
        transaction.getCategory().adjustBalancesForTransaction(transaction);
        transactionRepository.delete(transaction);
    }

    @Transactional
    public Transaction moveTransactionAccount(Long id, Account account) {
        Transaction transaction = transactionRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Transaction with that ID not found"));

        validateTransactionForMove(account);

        transaction.setAccount(account);

        return saveTransaction(transaction);

    }
    @Transactional
    public Transaction findTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("No transaction with that ID."));
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
    @Transactional
    public void transferFundsBetweenAccounts(Long sourceAccountId,TransferFundsRequest transferRequest) {
        if(transferRequest == null) {
            throw new IllegalArgumentException("Transfer Request should not be null.");
        }
        if(transferRequest.getDestinationAccountName() == null || transferRequest.getDestinationAccountName().isEmpty()) {
            throw new IllegalArgumentException("Destination name cannot be null or empty.");
        }
        Account sourceAccount = accountRepository.findByAccountId(sourceAccountId)
                .orElseThrow(() -> new RuntimeException("Source account with that ID not found."));
        Account destinationAccount = accountRepository.findByAccountName(transferRequest.getDestinationAccountName()).
                orElseThrow(() -> new RuntimeException("Destination account with that name is not found."));


        if(transferRequest.getDestinationAccountName().equals(sourceAccount.getAccountName())) {
            throw new RuntimeException("Source and Destination Account cannot be the same");
        }

        if(transferRequest.getTransferAmount() == null || transferRequest.getTransferAmount().compareTo(new BigDecimal("0.00")) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than 0 and must not be null.");
        }

        //create the transaction for the source account which will trigger a balance update
        createTransaction(sourceAccount.getAccountId(),null,null,transferRequest.getTransferAmount(),transferRequest.getDate(),"transfer to/from " + destinationAccount.getAccountName(),transferRequest.getTransactionType());

        if(transferRequest.getTransactionType() == TransactionType.INFLOW) {
            createTransaction(destinationAccount.getAccountId(),null,null,transferRequest.getTransferAmount(),transferRequest.getDate(),"transfer to " + sourceAccount.getAccountName(),TransactionType.OUTFLOW);
        }else {
            createTransaction(destinationAccount.getAccountId(),null,null,transferRequest.getTransferAmount(),transferRequest.getDate(),"transfer from " + sourceAccount.getAccountName(),TransactionType.INFLOW);
        }

    }

    //Private helper methods dealing with validation

    private void validateTransactionForMove(Account newAccount) {
        if(newAccount == null) throw new IllegalArgumentException("New account cannot be null.");

    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() == null) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        if (transaction.getAccount() == null) {
            throw new IllegalArgumentException("Transaction must be associated with an account.");
        }

        if (transaction.getDate() == null) {
            throw new IllegalArgumentException("Transaction date cannot be null.");
        }

        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Transaction type must be specified.");
        }
    }
}
