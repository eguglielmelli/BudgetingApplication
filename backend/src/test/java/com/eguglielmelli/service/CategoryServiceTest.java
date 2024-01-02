package com.eguglielmelli.service;

import com.eguglielmelli.models.Category;
import com.eguglielmelli.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void whenFindById_thenReturnCategory() {
        Long categoryId = 1L;
        Category category = spy(new Category()); // Create a spy on the Category object

        doReturn(categoryId).when(category).getCategoryId(); // Mock the getCategoryID() method

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Optional<Category> foundCategory = categoryService.findById(categoryId);

        assertTrue(foundCategory.isPresent());
        System.out.println(foundCategory.get().getCategoryId());
        assertEquals(categoryId, foundCategory.get().getCategoryId());
        assertEquals(category, foundCategory.get());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void whenFindById_withNonExistentId_thenReturnEmpty() {
        Long nonExistentCategoryId = 2L;

        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        Optional<Category> foundCategory = categoryService.findById(nonExistentCategoryId);

        assertFalse(foundCategory.isPresent());
        verify(categoryRepository).findById(nonExistentCategoryId);
    }
}

