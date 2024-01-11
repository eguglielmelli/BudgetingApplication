package com.eguglielmelli.integration;

import com.eguglielmelli.BudgetApp;
import com.eguglielmelli.models.*;
import com.eguglielmelli.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BudgetApp.class)
@AutoConfigureMockMvc
@Transactional // Roll back transactions after each test
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PayeeRepository payeeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Transaction testTransaction;
    private Account testAccount;
    private Payee testPayee;
    private Category testCategory;
    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setName("Transaction Integration Test User");
        testUser.setPassword("Password");
        testUser.setEmail("integrationtestuser@gmail.com");
        userRepository.save(testUser);

        testAccount = new Account();
        testAccount.setAccountName("Transaction Integration Test Account");
        testAccount.setAccountType("savings");
        testAccount.setUser(testUser);
        testAccount.setBalance(new BigDecimal("500.0"));
        testAccount = accountRepository.save(testAccount);

        testPayee = new Payee();
        testPayee.setName("Transaction Integration Test Payee");
        testPayee.setUser(testUser);
        testPayee = payeeRepository.save(testPayee);

        testCategory = new Category();
        testCategory.setBudgetedAmount(new BigDecimal("100.0"));
        testCategory.setName("Transaction Integration Test Category");
        testCategory.setUserID(testUser);
        testCategory.setSpent(new BigDecimal("0.0"));
        testCategory.setAvailable(new BigDecimal("100.0"));
        testCategory = categoryRepository.save(testCategory);

        testTransaction = new Transaction();
        testTransaction.setAccount(testAccount);
        testTransaction.setPayee(testPayee);
        testTransaction.setCategory(testCategory);
        testTransaction.setType(TransactionType.INFLOW);
        testTransaction.setDescription("Transaction Integration Test Transaction Description");
        testTransaction.setDate(new Date());
        testTransaction.setAmount(new BigDecimal("100.0"));
        testTransaction = transactionRepository.save(testTransaction);
    }

    @Test
    public void testCreateTransaction() throws Exception {
        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(testAccount);
        newTransaction.setPayee(testPayee);
        newTransaction.setCategory(testCategory);
        newTransaction.setType(TransactionType.INFLOW);
        newTransaction.setDescription("Transaction Integration Test Transaction Description");
        newTransaction.setDate(new Date());
        newTransaction.setAmount(new BigDecimal("100.0"));

        mockMvc.perform(post("/api/transactions/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTransaction)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        String updatedName = "Updated Transaction Description for Integration Test";
        testTransaction.setDescription(updatedName);
        mockMvc.perform(put("/api/transactions/" + testTransaction.getTransactionId() + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/" + testTransaction.getTransactionId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(testTransaction.getTransactionId()));
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/" + testTransaction.getTransactionId() + "/delete"))
                .andExpect(status().isOk());
    }
}
