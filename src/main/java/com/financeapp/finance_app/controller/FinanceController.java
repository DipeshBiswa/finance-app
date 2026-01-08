package com.financeapp.finance_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {
    public FinanceController() {

    }

    @GetMapping
    public String identifyUser(Principal principal){
        return "Hello "+ principal.getName() + ", you have accessed your private finance data";
    }


}
