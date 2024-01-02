//package com.eguglielmelli.service;
//
//import com.eguglielmelli.models.*;
//import com.eguglielmelli.repositories.TransactionRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class TransactionServiceTest {
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @Mock
//    private AccountService accountService;
//
//    @Mock
//    private PayeeService payeeService;
//
//    @Mock
//    private CategoryService categoryService;
//
//    @InjectMocks
//    private TransactionService transactionService;
//
//    private final Long accountId = 1L;
//    private final Long payeeId = 2L;
//    private final Long categoryId = 3L;
//    private BigDecimal amount;
//    private Date date;
//    private String description;
//    private TransactionType type;
//
//    @BeforeEach
//    void setUp() {
//        Account account = new Account();
//        Payee payee = new Payee();
//        Category category = new Category();
//        amount = BigDecimal.valueOf(100);
//        date = new Date();
//        description = "Test Transaction";
//        type = TransactionType.INFLOW;
//
//        when(accountService.findById(accountId)).thenReturn(Optional.of(account));
//        when(payeeService.findById(payeeId)).thenReturn(Optional.of(payee));
//        when(categoryService.findById(categoryId)).thenReturn(Optional.of(category));
//    }
//
//    @Test
//    public void whenCreateTransaction_thenSaveTransaction() {
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Transaction createdTransaction = transactionService.createTransaction(accountId, payeeId, categoryId, amount, date, description, type);
//
//        assertNotNull(createdTransaction);
//        assertEquals(amount, createdTransaction.getAmount());
//        verify(transactionRepository, times(1)).save(any(Transaction.class));
//    }
//
//    // Other test methods...
//}
