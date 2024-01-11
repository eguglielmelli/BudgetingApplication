package com.eguglielmelli.integration;
import com.eguglielmelli.BudgetApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eguglielmelli.models.Account;
import com.eguglielmelli.repositories.AccountRepository;
import com.eguglielmelli.repositories.UserRepository;
import com.eguglielmelli.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BudgetApp.class)
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setup() {
        String email = "test@example.com";
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            testUser = existingUser.get();
        } else {
            testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail(email);
            testUser.setPassword("test");
            userRepository.save(testUser);
        }
    }

    @Test
    @WithMockUser
    @Transactional
    public void testCreateAccount() throws Exception {
        Account newAccount = new Account();
        newAccount.setAccountName("Test Account");
        newAccount.setBalance(new BigDecimal("1000.00"));
        newAccount.setAccountType("checking");
        newAccount.setUser(testUser);

        mockMvc.perform(post("/api/accounts/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountName").value(newAccount.getAccountName()))
                .andExpect(jsonPath("$.balance").value(new BigDecimal("1000.0")))
                .andExpect(jsonPath("$.accountType").value(newAccount.getAccountType()));
    }

    @Test
    @WithMockUser
    @Transactional
    public void testGetAccountsForUser() throws Exception {
        Account existingAccount = new Account();
        existingAccount.setAccountName("Existing Account");
        existingAccount.setBalance(new BigDecimal("2000.00"));
        existingAccount.setAccountType("savings");
        existingAccount.setUser(testUser);
        accountRepository.save(existingAccount);

        mockMvc.perform(get("/api/users/{userId}/accounts", testUser.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(existingAccount.getAccountId()))
                .andExpect(jsonPath("$[0].accountName").value("Existing Account"))
                .andExpect(jsonPath("$[0].balance").value(2000.00))
                .andExpect(jsonPath("$[0].accountType").value("savings"));
    }

    // Additional tests can be added here
}