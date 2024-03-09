package com.eguglielmelli.models;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private long transactionId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "AccountID", referencedColumnName = "AccountID")
    private Account account;

    @ManyToOne
    @JoinColumn(name="PayeeID",referencedColumnName = "PayeeID")
    private Payee payee;

    @ManyToOne
    @JoinColumn(name = "CategoryID", referencedColumnName = "CategoryID")
    private Category category;

    @Column(name = "Amount")
    private BigDecimal amount;

    @Column(name = "Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "Description")
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Transient
    private TransactionAction action;

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    public long getTransactionId() {
        return transactionId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setAction(TransactionAction action) {
        this.action = action;
    }
    public TransactionAction getAction() {
        return this.action;
    }
}
