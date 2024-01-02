package com.eguglielmelli.service;

import com.eguglielmelli.models.Category;
import com.eguglielmelli.models.Transaction;
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
                .filter(Objects::nonNull)  // Ensure the amount is not null
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Sum up the amounts

        return totalSpent;
    }
    @Transactional
    public BigDecimal calculateAvailableAmount(Long categoryId,Long userId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category does not exist."));
        BigDecimal spent = calculateTotalSpentInCategory(categoryId,userId);
        return category.getAvailable().subtract(spent);
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

    public Optional<Category> findById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }
}
