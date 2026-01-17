package com.financeapp.finance_app.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Goal {

    @GeneratedValue
    @Id
    private Long id;
    private String title;
    private String description;
    @CreationTimestamp
    private LocalDateTime date;
    private boolean is_budget;
    private BigDecimal current_amount;
    private BigDecimal target_amount;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;
}
