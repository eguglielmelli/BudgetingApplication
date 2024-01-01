package com.eguglielmelli.service;
import com.eguglielmelli.models.*;
import com.eguglielmelli.repositories.TransactionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;
    private Payee payee;
    private Category category;
    private BigDecimal amount;
    private Date date;
    private String description;
    private TransactionType type;

    @BeforeEach
    void setUp() {
        account = new Account();
        payee = new Payee();
        category = new Category();
        amount = BigDecimal.valueOf(100);
        date = new Date();
        description = "Test Transaction";
        type = TransactionType.INFLOW;
    }

    @Test
    public void whenCreateTransaction_thenSaveTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setPayee(payee);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction createdTransaction = transactionService.createTransaction(account, payee, category, amount, date, description, type);

        assertNotNull(createdTransaction);
        assertEquals(amount, createdTransaction.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void whenFindTransactionsByCategoryWithNullCategory_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.findTransactionsByCategory(null);
        });
    }

    @Test
    void whenUpdateTransaction_thenTransactionIsUpdated() {
        Long transactionId = 1L;
        Transaction existingTransaction = new Transaction();
        existingTransaction.setAccount(account);
        existingTransaction.setPayee(payee);
        existingTransaction.setCategory(category);
        existingTransaction.setAmount(amount);
        existingTransaction.setDate(date);
        existingTransaction.setDescription(description);
        existingTransaction.setType(type);

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAccount(account); // Assume updated account
        updatedTransaction.setPayee(payee); // Assume updated payee
        updatedTransaction.setCategory(category); // Assume updated category
        updatedTransaction.setAmount(BigDecimal.valueOf(200)); // Updated amount
        updatedTransaction.setDate(date); // Assume updated date
        updatedTransaction.setDescription("Updated Description"); // Updated description
        updatedTransaction.setType(TransactionType.OUTFLOW); // Updated type

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        Transaction resultTransaction = transactionService.update(transactionId, updatedTransaction);

        assertNotNull(resultTransaction);
        assertEquals(BigDecimal.valueOf(200), resultTransaction.getAmount());
        assertEquals("Updated Description", resultTransaction.getDescription());
        assertEquals(TransactionType.OUTFLOW, resultTransaction.getType());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    @Test
    void whenTransactionDescriptionIsEmpty() {
        Transaction existingTransaction = new Transaction();
        existingTransaction.setAccount(account);
        existingTransaction.setPayee(payee);
        existingTransaction.setCategory(category);
        existingTransaction.setAmount(amount);
        existingTransaction.setDate(date);
        existingTransaction.setDescription("");
        existingTransaction.setType(type);

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.save(existingTransaction);
        });
    }

}
