package com.eguglielmelli.models;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="CategoryID")
    private long CategoryID;

    @ManyToOne
    @JoinColumn(name="UserID",referencedColumnName = "UserID")
    private User user;

    @Column(name="Name")
    private String name;

    @Column(name="BudgetedAmount")
    private BigDecimal BudgetedAmount;

    @Column(name="Available")
    private BigDecimal available;

    @Column(name="Spent")
    private BigDecimal spent;

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

    public long getCategoryID() {
        return CategoryID;
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
        return BudgetedAmount;
    }

    public void setBudgetedAmount(BigDecimal budgetedAmount) {
        BudgetedAmount = budgetedAmount;
    }
}