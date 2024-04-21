package com.eguglielmelli.service;
import com.eguglielmelli.models.Budget;
import com.eguglielmelli.models.Transaction;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.BudgetRepository;
import com.eguglielmelli.repositories.CategoryRepository;
import com.eguglielmelli.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository,TransactionRepository transactionRepository,CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Budget createBudget(User user, Date timePeriod, BigDecimal totalIncome, BigDecimal totalExpense) {

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setTimePeriod(timePeriod);
        budget.setTotalIncome(totalIncome);
        budget.setTotalExpense(totalExpense);

        validateBudget(budget);

        return budgetRepository.save(budget);
    }

    @Transactional
    public boolean deleteBudget(Long budgetId) {
        Optional<Budget> budget = budgetRepository.findById(budgetId);
        if (budget.isPresent()) {
            budgetRepository.delete(budget.get());
            return true;
        }
        return false;
    }

    @Transactional
    public void calculateAndUpdateBudgetTotals(Budget budget) {
        if (budget == null || budget.getUser() == null || budget.getTimePeriod() == null) {
            throw new IllegalArgumentException("Budget, User, and Time Period must not be null.");
        }

        Date startDate = getStartOfMonth(budget.getTimePeriod());
        Date endDate = getEndOfMonth(budget.getTimePeriod());

        List<Transaction> transactions = transactionRepository.findByCategory_User_UserIdAndDateBetween(
                budget.getUser().getUserId(), startDate, endDate);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else {
                totalExpense = totalExpense.add(transaction.getAmount().abs());
            }
        }

        budget.setTotalIncome(totalIncome);
        budget.setTotalExpense(totalExpense);

        budgetRepository.save(budget);
    }
    @Transactional
    public Optional<Budget> findById(Long budgetId) {
        return Optional.ofNullable(budgetRepository.findById(budgetId).orElseThrow(() -> new IllegalArgumentException("Budget with that ID not found.")));
    }

    public void validateBudget(Budget budget) {
        if(budget == null) {
            throw new IllegalArgumentException("Budget cannot be null.");
        }

        if (budget.getTotalIncome() == null || budget.getTotalIncome().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("TotalIncome cannot be null.");
        }

        if (budget.getTotalExpense() == null || budget.getTotalExpense().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("TotalExpense cannot be null.");
        }

        if(budget.getUser() == null) {
            throw new IllegalArgumentException("UserId cannot be null.");
        }
    }

    private Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }


}
