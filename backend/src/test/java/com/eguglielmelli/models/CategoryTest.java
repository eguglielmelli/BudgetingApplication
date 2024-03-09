package com.eguglielmelli.models;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;



public class CategoryTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Category Test User");
        user.setUserId(1L);
    }
    @Test
    public void testApplyTransactionEffect() {

        //Testing when we are creating an outflow transaction
        //that spent and available will be adjusted accordingly
        Category category = new Category();
        category.setBudgetedAmount(new BigDecimal("1000.00"));
        category.setAvailable(new BigDecimal("500.00"));
        category.setSpent(new BigDecimal("500.00"));
        category.setUserID(user);

        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setAction(TransactionAction.CREATE);
        transaction.setType(TransactionType.OUTFLOW);

        category.applyTransactionEffect(transaction);

        assertEquals(category.getSpent(),new BigDecimal("1000.00"));
        assertEquals(category.getAvailable(),new BigDecimal("0.00"));


        //Testing for creating an inflow transaction, spent and available adjusted accordingly
        Category category1 = new Category();
        category1.setBudgetedAmount(new BigDecimal("500.00"));
        category1.setSpent(new BigDecimal("100.00"));
        category1.setAvailable(new BigDecimal("400.00"));

        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionType.INFLOW);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setAction(TransactionAction.CREATE);

        category1.applyTransactionEffect(transaction1);

        assertEquals(category1.getSpent(),new BigDecimal("0.00"));
        assertEquals(category1.getAvailable(),new BigDecimal("500.00"));


        //Testing for updating an outflow transaction
        Category category2 = new Category();
        category2.setBudgetedAmount(new BigDecimal("500.00"));
        category2.setSpent(new BigDecimal("100.00"));
        category2.setAvailable(new BigDecimal("400.00"));


        //simulate change in the transactions without looking up in DB
        category2.setSpent(new BigDecimal("0.00"));

        Transaction transaction2 = new Transaction();
        transaction2.setType(TransactionType.OUTFLOW);
        transaction2.setAmount(new BigDecimal("50.00"));
        transaction2.setAction(TransactionAction.UPDATE);

        category2.applyTransactionEffect(transaction2);

        assertEquals(category2.getSpent(),new BigDecimal("50.00"));
        assertEquals(category2.getAvailable(),new BigDecimal("450.00"));

    }
    @Test
    void testReverseTransactionEffect() {

        //test deleting an outflow transaction
        //expecting spent to go down and available to go up
        Category category = new Category();
        category.setBudgetedAmount(new BigDecimal("750.00"));
        category.setAvailable(new BigDecimal("550.00"));
        category.setSpent(new BigDecimal("200.00"));
        category.setUserID(user);

        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setAction(TransactionAction.DELETE);
        transaction.setType(TransactionType.OUTFLOW);

        category.reverseTransactionEffect(transaction);

        assertEquals(new BigDecimal("100.00"),category.getSpent());
        assertEquals(new BigDecimal("650.00"),category.getAvailable());


        //Deleting an inflow transaction
        //expecting spent and available to decrease accordingly
        Category category1 = new Category();
        category1.setBudgetedAmount(new BigDecimal("500.00"));
        category1.setSpent(new BigDecimal("100.00"));
        category1.setAvailable(new BigDecimal("600.00"));

        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionType.INFLOW);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setAction(TransactionAction.DELETE);

        category1.applyTransactionEffect(transaction1);

        assertEquals(category1.getSpent(),new BigDecimal("0.00"));
        assertEquals(category1.getAvailable(),new BigDecimal("500.00"));
    }
    @Test
    void testAdjustBudgetedAndAvailableAmount() {

        //testing ADD to budget, should adjust to 850 budgeted, 700 available
        Category category = new Category();
        category.setBudgetedAmount(new BigDecimal("750.00"));
        category.setAvailable(new BigDecimal("600.00"));
        category.setSpent(new BigDecimal("150.00"));
        category.setUserID(user);

        category.adjustBudgetedAndAvailableAmount(CategoryBudgetAction.ADD,new BigDecimal("100.00"));

        assertEquals(new BigDecimal("850.00"),category.getBudgetedAmount());
        assertEquals(new BigDecimal("700.00"),category.getAvailable());

        //Testing SUBTRACT to budget, should adjust budgeted to 700 and available to 0.00
        Category category1 = new Category();
        category1.setBudgetedAmount(new BigDecimal("800.00"));
        category1.setSpent(new BigDecimal("700.00"));
        category1.setAvailable(new BigDecimal("100.00"));

        category1.adjustBudgetedAndAvailableAmount(CategoryBudgetAction.SUBTRACT,new BigDecimal("100.00"));

        assertEquals(new BigDecimal("700.00"),category1.getBudgetedAmount());
        assertEquals(new BigDecimal("0.00"),category1.getAvailable());


        //Testing SET where ending available should be negative
        Category category2 = new Category();
        category2.setBudgetedAmount(new BigDecimal("700.00"));
        category2.setSpent(new BigDecimal("500.00"));
        category2.setAvailable(new BigDecimal("200.00"));

        category2.adjustBudgetedAndAvailableAmount(CategoryBudgetAction.SET,new BigDecimal("300.00"));

        assertEquals(new BigDecimal("300.00"),category2.getBudgetedAmount());
        assertEquals(new BigDecimal("-200.00"),category2.getAvailable());

        //Testing SET with positive values
        Category category3 = new Category();
        category3.setBudgetedAmount(new BigDecimal("600.00"));
        category3.setSpent(new BigDecimal("500.00"));
        category3.setAvailable(new BigDecimal("100.00"));

        category3.adjustBudgetedAndAvailableAmount(CategoryBudgetAction.SET,new BigDecimal("1000.00"));

        assertEquals(new BigDecimal("1000.00"),category3.getBudgetedAmount());
        assertEquals(new BigDecimal("500.00"),category3.getAvailable());

    }
}
