package com.eguglielmelli.integration;

import com.eguglielmelli.BudgetApp;
import com.eguglielmelli.models.Payee;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.PayeeRepository;
import com.eguglielmelli.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BudgetApp.class)
@AutoConfigureMockMvc
@Transactional
public class PayeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PayeeRepository payeeRepository;

    @Autowired
    private UserRepository userRepository;

    private Payee testPayee;

    private User testUser;

    @BeforeEach
    void setup() {
        // Create a test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setPassword("test123");
        testUser.setEmail("testuser@example.com");
        testUser = userRepository.save(testUser); // Save and retrieve the saved entity with ID

        // Create a test payee
        testPayee = new Payee();
        testPayee.setName("Test Payee");
        testPayee.setUser(testUser);
        testPayee = payeeRepository.save(testPayee);
    }

    @Test
    public void testCreatePayee() throws Exception {
        Payee newPayee = new Payee();
        newPayee.setName("New Test Payee");
        newPayee.setUser(testUser);

        mockMvc.perform(post("/api/payee/" + testUser.getUserId()) // Use the test user's ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPayee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Test Payee"));
    }

    @Test
    public void testUpdatePayeeName() throws Exception {
        testPayee.setName("Updated Test Payee");

        mockMvc.perform(put("/api/payee/" + testPayee.getPayeeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Test Payee"));
    }

    @Test
    public void testDeletePayee() throws Exception {
        mockMvc.perform(delete("/api/payee/" + testPayee.getPayeeId()))
                .andExpect(status().isOk());
    }
}
