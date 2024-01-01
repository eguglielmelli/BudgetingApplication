package com.eguglielmelli.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Payee")
public class Payee {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PayeeID")
    private Long payeeId;

    @Column(name = "Name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    private User user;

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payee)) return false;
        Payee payee = (Payee) o;
        return Objects.equals(payeeId, payee.payeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payeeId);
    }
}