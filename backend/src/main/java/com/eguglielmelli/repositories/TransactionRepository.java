package com.eguglielmelli.repositories;
import com.eguglielmelli.models.Category;
import com.eguglielmelli.models.Payee;
import com.eguglielmelli.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByPayee(Payee payee);

    List<Transaction> findByDateBetween(Date startDate, Date endDate);

    List<Transaction> findByCategory_User_UserIdAndCategory_CategoryId(Long userId, Long categoryId);

    List<Transaction> findByCategory_User_UserIdAndDateBetween(Long userId,Date startDate,Date endDate);
}
