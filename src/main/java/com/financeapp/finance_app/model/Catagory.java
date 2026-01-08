package com.financeapp.finance_app.model;

import lombok.Getter;

@Getter
public enum Catagory {
    FOODDINING("Food & Dining"),
    SHOPPING("Shopping"),
    TRANSPORTATION("Transportation"),
    HOUSING("Housing"),
    UTILITY("Utility"),
    ENTERTAINMENT("Entertainment"),
    OTHER("Other");

    public String names;

    private Catagory(String names) {
        this.names = names;
    }
}
