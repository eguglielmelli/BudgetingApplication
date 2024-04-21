package com.eguglielmelli.repositories;
import com.eguglielmelli.models.Category;
import com.eguglielmelli.models.Payee;
import com.eguglielmelli.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByPayee(Payee payee);

    List<Transaction> findByDateBetween(Date startDate, Date endDate);

    @Query("SELECT t FROM Transaction t WHERE t.category.id = :categoryId AND t.category.user.id = :userId")
    List<Transaction> findTransactionsByCategoryIdAndUserId(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    List<Transaction> findByCategory_User_UserIdAndDateBetween(Long userId,Date startDate,Date endDate);
}
