package com.eguglielmelli.integration;

import com.eguglielmelli.BudgetApp;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BudgetApp.class)
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }


    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testCreateUser() throws Exception {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpassword");

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New User"));
    }

    @Test
    public void testUpdatePassword() throws Exception {
        String oldPassword = "password"; // Use the raw password here
        testUser.setPassword(passwordEncoder.encode(oldPassword));
        userRepository.save(testUser);
        String newPassword = "newPassword";

        mockMvc.perform(put("/api/users/" + testUser.getUserId() + "/password")
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteAccount() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getUserId()))
                .andExpect(status().isOk());
    }
}