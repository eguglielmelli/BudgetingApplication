package com.eguglielmelli.repositories;
import com.eguglielmelli.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<Category> findByName(String name);

    List<Category> findByUserUserId(Long userId);

    List<Category> findByBudgetBudgetId(Long budgetId);
}
