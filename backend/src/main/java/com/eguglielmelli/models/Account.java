package com.eguglielmelli.models;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private long accountId;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    private User user;

    @Column(name = "AccountName")
    private String accountName;

    @Column(name = "AccountType")
    private String accountType;

    @Column(name = "Balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "BudgetID", referencedColumnName = "BudgetID")
    private Budget budget;

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }
    public Budget getBudget() {
        return this.budget;
    }

    public void adjustBalanceForTransaction(Transaction transaction) {
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
        if(transaction.getType() == TransactionType.INFLOW) {
            this.setBalance(this.balance.add(transaction.getAmount()));
        }else {
            this.setBalance(this.balance.subtract(transaction.getAmount()));
        }
    }
    public void reverseTransactionEffect(Transaction transaction) {
        if(transaction.getType() == TransactionType.INFLOW) {
            this.setBalance(this.balance.subtract(transaction.getAmount()));
        }else {
            this.setBalance(this.balance.add(transaction.getAmount()));
        }
    }
}
