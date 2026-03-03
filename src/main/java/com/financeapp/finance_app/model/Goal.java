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
public class Goal {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;
    private String description;
    @CreationTimestamp
    private LocalDateTime date;
    private BigDecimal current_amount;
    private BigDecimal target_amount;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;
    @Enumerated(EnumType.STRING)
    private Catagory category;

    public Goal(){}
    public Goal(String title, String description, BigDecimal target_amount, LocalDateTime date, Catagory category) {
        this.title = title;
        this.description = description;
        this.target_amount = target_amount;
        this.date = date;
        this.current_amount = BigDecimal.ZERO;
        this.category = category;
    }

}
