package com.eguglielmelli.repository;

import com.eguglielmelli.models.Category;
import com.eguglielmelli.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void whenFindById_thenReturnCategory() {
        // Given
//        Category category = new Category();
//        category.setName("Groceries");
//        // ... set other properties
//        category = categoryRepository.save(category);

        // When
        Optional<Category> foundCategory = categoryRepository.findById(Long.valueOf(1));

        // Then
        assertThat(foundCategory).isNotEmpty();
        assertThat(foundCategory.get().getName()).isEqualTo("Groceries");
    }

    @Test
    public void whenFindById_withNonExistentId_thenReturnEmpty() {
        // Given
        Long nonExistentId = 999L;

        // When
        Optional<Category> foundCategory = categoryRepository.findById(nonExistentId);

        // Then
        assertThat(foundCategory).isEmpty();
    }
}
