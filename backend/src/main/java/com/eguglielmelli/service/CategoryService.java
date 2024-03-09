package com.eguglielmelli.service;

import com.eguglielmelli.models.Category;
import com.eguglielmelli.models.CategoryBudgetAction;
import com.eguglielmelli.models.Transaction;
import com.eguglielmelli.models.TransactionType;
import com.eguglielmelli.repositories.CategoryRepository;
import com.eguglielmelli.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }
    @Transactional
    public Category createCategory(Category category) {
        boolean categoryExists = categoryRepository.findByName(category.getName()).isPresent();
        if (categoryExists) {
            throw new RuntimeException("Category with that name already exists.");
        }
        Category newCategory = new Category();
        newCategory.setName(category.getName());
        newCategory.setAvailable(category.getAvailable());
        newCategory.setSpent(category.getSpent());
        newCategory.setBudgetedAmount(category.getBudgetedAmount());
        newCategory.setUserID(category.getUser());
        return categoryRepository.save(newCategory);
    }

    public List<Transaction> getAllTransactions(Long categoryId,Long userId) {
        return transactionRepository.findByCategory_User_UserIdAndCategory_CategoryId(categoryId,userId);
    }
    @Transactional
    public BigDecimal calculateTotalSpentInCategory(Long categoryId,Long userId) {
        List<Transaction> transactions = transactionRepository.findByCategory_User_UserIdAndCategory_CategoryId(userId,categoryId);
        BigDecimal totalSpent = transactions.stream()
                .map(Transaction::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalSpent;
    }
    @Transactional
    public BigDecimal calculateAvailableAmount(Long categoryId,Long userId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category does not exist."));
        BigDecimal spent = calculateTotalSpentInCategory(categoryId,userId);
        return category.getBudgetedAmount().subtract(spent);
    }
    @Transactional
    public Category changeCategoryName(Long categoryId,String updatedName) {
        Category foundCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category doesn't exist."));
        if(foundCategory.getName() == null || foundCategory.getName().isEmpty() || updatedName == null || updatedName.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty or null");
        }
        foundCategory.setName(updatedName);
        return categoryRepository.save(foundCategory);
    }
    @Transactional
    public Category updateBudgetAmount(Long id, CategoryBudgetAction action,BigDecimal amount) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category with that ID does not exist."));
        category.adjustBudgetedAndAvailableAmount(action,amount);
        return category;
    }
    @Transactional
    private void adjustCategoryBalance(Category category, Transaction transaction) {
        BigDecimal transactionAmount = transaction.getAmount();

        if (transaction.getType() == TransactionType.INFLOW) {
            category.setSpent(category.getSpent().subtract(transactionAmount));
        } else if (transaction.getType() == TransactionType.OUTFLOW) {
            category.setSpent(category.getSpent().add(transactionAmount));
        }

        category.setAvailable(category.getBudgetedAmount().subtract(category.getSpent()));

        categoryRepository.save(category);
    }

    public Optional<Category> findById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }
}
