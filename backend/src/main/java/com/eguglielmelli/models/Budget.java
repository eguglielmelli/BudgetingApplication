package com.eguglielmelli.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BudgetID")
    private long budgetId;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    private User user;

    @Column(name = "TimePeriod")
    private Date timePeriod;

    @Column(name = "TotalIncome")
    private BigDecimal totalIncome;

    @Column(name = "TotalExpense")
    private BigDecimal totalExpense;

    @Column(name = "readyToAssign", precision=10, scale=2, nullable=false)
    private BigDecimal readyToAssign;

    public long getBudgetId() {
        return budgetId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(Date timePeriod) {
        this.timePeriod = timePeriod;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getReadyToAssign() {
        return readyToAssign;
    }
    public void setReadyToAssign(BigDecimal readyToAssign) {
        this.readyToAssign = readyToAssign;
    }

}