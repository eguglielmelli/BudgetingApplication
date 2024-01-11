package com.eguglielmelli.integration;

import com.eguglielmelli.BudgetApp;
import com.eguglielmelli.models.Category;
import com.eguglielmelli.repositories.CategoryRepository;
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
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BudgetApp.class)
@AutoConfigureMockMvc
@Transactional // Roll back transactions after each test
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setup() {
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setBudgetedAmount(new BigDecimal("500.0"));
        testCategory = categoryRepository.save(testCategory); // Save the category to the database
    }

    @Test
    public void testCreateCategory() throws Exception {
        Category newCategory = new Category();
        newCategory.setName("New Test Category");
        newCategory.setBudgetedAmount(new BigDecimal("1000.0"));

        mockMvc.perform(post("/api/categories/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Test Category"));
    }

    @Test
    public void testUpdateCategoryName() throws Exception {
        Map<String, String> updateInfo = new HashMap<>();
        updateInfo.put("updatedName", "Updated Category Name");

        mockMvc.perform(put("/api/categories/" + testCategory.getCategoryId() + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Category Name"));
    }

    @Test
    public void testFindCategory() throws Exception {
        mockMvc.perform(get("/api/categories/" + testCategory.getCategoryId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Category"));
    }
}
