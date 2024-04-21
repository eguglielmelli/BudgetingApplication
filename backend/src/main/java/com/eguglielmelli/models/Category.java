package com.eguglielmelli.models;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="CategoryID")
    private Long categoryId;

    @ManyToOne
    @JoinColumn(name="UserID",referencedColumnName = "UserID")
    private User user;

    @Column(name="Name")
    private String name;

    @Column(name="BudgetedAmount")
    private BigDecimal budgetedAmount;

    @Column(name="Available")
    private BigDecimal available;

    @Column(name="Spent")
    private BigDecimal spent;

    @ManyToOne
    @JoinColumn(name = "BudgetID", referencedColumnName = "BudgetID")
    private Budget budget;

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }


    public User getUser() {
        return user;
    }

    public void setUserID(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudgetedAmount() {
        return budgetedAmount;
    }

    public void setBudgetedAmount(BigDecimal budgetedAmount) {
        this.budgetedAmount = budgetedAmount;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }
    public Budget getBudget() {
        return this.budget;
    }


    public void adjustBalancesForTransaction(Transaction transaction) {
        switch(transaction.getAction()) {
            case CREATE:
            case UPDATE:
                applyTransactionEffect(transaction);
                break;
            case DELETE:
                reverseTransactionEffect(transaction);
                break;
        }
    }
    
    public void applyTransactionEffect(Transaction transaction) {
        if(transaction.getType() == TransactionType.OUTFLOW) {
            this.setSpent(this.spent.add(transaction.getAmount()));
        }else {
            this.setSpent(this.spent.subtract(transaction.getAmount()));
        }
        this.setAvailable(this.budgetedAmount.subtract(this.spent));
    }

    public void reverseTransactionEffect(Transaction transaction) {
        if(transaction.getType() == TransactionType.OUTFLOW) {
            this.setSpent(this.spent.subtract(transaction.getAmount()));
        }else {
            this.setSpent(this.spent.add(transaction.getAmount()));
        }
        this.setAvailable(this.budgetedAmount.subtract(this.spent));
    }
    public void adjustBudgetedAndAvailableAmount(CategoryBudgetAction action,BigDecimal amount) {
        switch(action) {
            case ADD:
                this.setBudgetedAmount(this.budgetedAmount.add(amount));
                break;
            case SUBTRACT:
                this.setBudgetedAmount(this.budgetedAmount.subtract(amount));
                break;
            case SET:
                this.setBudgetedAmount(amount);
                break;
        }
        this.setAvailable(this.budgetedAmount.subtract(this.spent));
    }
}
