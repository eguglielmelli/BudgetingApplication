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

    public void adjustBalanceForTransaction(BigDecimal amount) {
        BigDecimal currBalance = this.getBalance();
        this.setBalance(currBalance.add(amount));
    }
}
