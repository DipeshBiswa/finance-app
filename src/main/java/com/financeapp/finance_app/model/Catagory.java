package com.financeapp.finance_app.model;

import lombok.Getter;

import java.util.List;

@Getter
public enum Catagory {
    FOODDINING("Food & Dining"),
    SHOPPING("Shopping"),
    TRANSPORTATION("Transportation"),
    RENT_AND_BILLS("Rent & Billing"),
    INCOME("Income"),
    OTHER("Other");

    public String names;

    private Catagory(String names) {
        this.names = names;
    }

    public static Catagory fromPlaid(String plaidPrimary){
        if(plaidPrimary == null) return null;
        return switch(plaidPrimary){
            case "FOOD_AND_DRINK" -> FOODDINING;
            case "TRANSPORTATION" -> TRANSPORTATION;
            case "RENT_AND_UTILITIES" -> RENT_AND_BILLS;
            case "INCOME", "TRANSFER_IN" -> INCOME;
            case "GENERAL_MERCHANDISE" -> SHOPPING;
            default -> OTHER;
        };
    }
}
