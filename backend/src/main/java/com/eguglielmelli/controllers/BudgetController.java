package com.eguglielmelli.controllers;

import com.eguglielmelli.models.Budget;
import com.eguglielmelli.service.BudgetService;
import com.eguglielmelli.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    @Autowired
    public BudgetController(BudgetService budgetService, UserService userService) {
        this.budgetService = budgetService;
        this.userService = userService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Budget> createBudget(@PathVariable Long userId, @RequestBody Budget budgetRequest) {
        return userService.findById(userId).map(user -> {
            Budget createdBudget = budgetService.createBudget(
                    user,
                    budgetRequest.getTimePeriod(),
                    budgetRequest.getTotalIncome(),
                    budgetRequest.getTotalExpense()
            );
            return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/budget/{budgetId}/totals")
    public ResponseEntity<?> updateAndGetBudgetTotals(@PathVariable Long budgetId) {
        return budgetService.findById(budgetId).map(budget -> {
            budgetService.calculateAndUpdateBudgetTotals(budget);
            return ResponseEntity.ok(budget);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{budgetId}/delete")
    public ResponseEntity<?> deleteBudget(@PathVariable Long budgetId) {
        return budgetService.findById(budgetId).map(budget -> {
            budgetService.deleteBudget(budgetId);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

