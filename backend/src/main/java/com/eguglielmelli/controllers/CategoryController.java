package com.eguglielmelli.controllers;
import com.eguglielmelli.models.Category;
import com.eguglielmelli.models.Transaction;
import com.eguglielmelli.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        Category newCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}/update")
    public ResponseEntity<?> updateCategoryName(@PathVariable Long categoryId,@RequestBody Map<String, String> updateInfo) {
        String updatedName = updateInfo.get("updatedName");
        try {
            Category category = categoryService.changeCategoryName(categoryId, updatedName);

            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating category: " + e.getMessage());
        }
    }


    @GetMapping("/{categoryId}")
    public ResponseEntity<?> findCategory(@PathVariable Long categoryId) {
        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{categoryId}/transactions/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserAndCategory(
            @PathVariable Long categoryId,
            @PathVariable Long userId) {
        try {
            List<Transaction> transactions = categoryService.getAllTransactions(userId, categoryId);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{categoryId}/user/{userId}/totalSpent")
    public ResponseEntity<?> getTotalSpentInCategoryByUser(
            @PathVariable Long categoryId,
            @PathVariable Long userId) {
        try {
            BigDecimal totalSpent = categoryService.calculateTotalSpentInCategory(categoryId, userId);
            return new ResponseEntity<>(totalSpent, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{categoryId}/user/{userId}/amountAvailable")
    public ResponseEntity<?> getAvailableAmount(
            @PathVariable Long categoryId,
            @PathVariable Long userId) {
        try {
            BigDecimal totalSpent = categoryService.calculateAvailableAmount(categoryId, userId);
            return new ResponseEntity<>(totalSpent, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{categoryId}/updateAmount")
    public ResponseEntity<Category> updateBudgetedAmount(@PathVariable Long categoryId,@RequestBody  Map<String,BigDecimal> updatedAmount) {
        BigDecimal newAmount = updatedAmount.get("updatedAmount");
        Category category = categoryService.updateBudgetAmount(categoryId,newAmount);
        return ResponseEntity.ok(category);
    }


}
