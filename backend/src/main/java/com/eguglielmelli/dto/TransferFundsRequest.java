package com.eguglielmelli.dto;
import com.eguglielmelli.models.TransactionType;

import java.util.Date;
import java.math.BigDecimal;

public class TransferFundsRequest {
    private String destinationAccountName;
    private BigDecimal transferAmount;
    private Date date;
    private TransactionType transactionType;

    public String getDestinationAccountName() {
        return destinationAccountName;
    }

    public void setDestinationAccountName(String destinationAccountName) {
        this.destinationAccountName = destinationAccountName;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}

