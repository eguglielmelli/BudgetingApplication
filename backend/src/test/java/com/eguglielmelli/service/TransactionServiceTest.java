package com.eguglielmelli.service;

import com.eguglielmelli.models.*;
import com.eguglielmelli.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PayeeRepository payeeRepository;

    @Mock
    private CategoryRepository categoryRepository;


    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;
    private Payee testPayee;
    private Category testCategory;
    private User testUser;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Transaction Service Test User");
        testUser.setPassword("Password");
        testUser.setEmail("integrationtestuser@gmail.com");

        testAccount = new Account();
        testAccount.setAccountName("Transaction Service Test Account");
        testAccount.setAccountType("savings");
        testAccount.setUser(testUser);
        testAccount.setBalance(new BigDecimal("500.0"));


        testPayee = new Payee();
        testPayee.setName("Transaction Integration Test Payee");
        testPayee.setUser(testUser);


        testCategory = new Category();
        testCategory.setBudgetedAmount(new BigDecimal("100.0"));
        testCategory.setName("Transaction Service Test Category");
        testCategory.setUserID(testUser);
        testCategory.setSpent(new BigDecimal("0.0"));
        testCategory.setAvailable(new BigDecimal("100.0"));


        testTransaction = new Transaction();
        testTransaction.setDescription("Transaction Service Test Description");
        testTransaction.setPayee(testPayee);
        testTransaction.setAccount(testAccount);
        testTransaction.setCategory(testCategory);
        Long testTransactionId = 1L;
        testTransaction.setTransactionId(testTransactionId);

        lenient().when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));
        lenient().when(payeeRepository.findById(anyLong())).thenReturn(Optional.of(testPayee));
        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        lenient().when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void whenCreateTransaction_thenSaveTransaction() {

        BigDecimal amount = BigDecimal.valueOf(100);
        Date date = new Date();
        String description = "Test Transaction";
        TransactionType type = TransactionType.INFLOW;

        Transaction createdTransaction = transactionService.createTransaction(1L, 2L, 3L, amount, date, description, type);


        assertNotNull(createdTransaction);
        assertEquals(amount, createdTransaction.getAmount());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void whenUpdateTransaction_thenUpdated() {

        Long transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(BigDecimal.valueOf(200));
        updatedTransaction.setAccount(testAccount);
        updatedTransaction.setCategory(testCategory);
        updatedTransaction.setType(TransactionType.INFLOW);
        updatedTransaction.setDate(new Date());
        updatedTransaction.setDescription("Update Transaction Description");
        updatedTransaction.setPayee(testPayee);

        Transaction result = transactionService.update(transactionId, updatedTransaction);


        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(200), result.getAmount());
        verify(transactionRepository).save(testTransaction);
    }
    @Test
    void whenDeleteTransaction_thenSuccessfullyRemoved() {
        Long transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));


        transactionService.delete(transactionId);


        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).delete(testTransaction);
    }
    @Test
    void whenMoveTransactionAccount_thenAccountUpdated() {
        // Arrange
        Long transactionId = 1L;
        Account newAccount = new Account();
        newAccount.setAccountName("New Account");
        newAccount.setAccountType("checking");
        newAccount.setUser(testUser);
        newAccount.setBalance(new BigDecimal("1000.0"));

        Transaction existingTransaction = new Transaction();
        existingTransaction.setTransactionId(transactionId);
        existingTransaction.setAccount(testAccount);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));

        // Act
        Transaction updatedTransaction = transactionService.moveTransactionAccount(transactionId, newAccount);

        // Assert
        assertNotNull(updatedTransaction);
        assertEquals(newAccount, updatedTransaction.getAccount());
        verify(transactionRepository).save(updatedTransaction);
    }



    // Additional tests for delete, find, and exception scenarios
}
