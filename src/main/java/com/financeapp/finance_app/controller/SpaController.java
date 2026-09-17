package com.financeapp.finance_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/", "/login", "/register", "/dashboard", "/goals"})
    public String index() {
        return "forward:/index.html";
    }
}
