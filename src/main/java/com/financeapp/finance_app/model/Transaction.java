package com.financeapp.finance_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Transaction {

    @GeneratedValue
    @Id
    private Long id;
    private String description;
    private BigDecimal amount;
    @CreationTimestamp
    private LocalDateTime date;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;
    @Enumerated(EnumType.STRING)
    private Catagory catagory;
    @Column(unique = true, nullable = false)
    private String plaidTransactionId;

    public Transaction(String description, BigDecimal amount, Catagory catagory, LocalDateTime date, String plaidTransactionId) {
        this.description = description;
        this.amount = amount;
        this.catagory = catagory;
        this.date = date;
        this.plaidTransactionId = plaidTransactionId;
    }
    public Transaction() {}



}
