package com.financeapp.finance_app.service;

import com.financeapp.finance_app.model.user;

public class JwtService {
    private final UserService userService;
    public JwtService(UserService userService) {
        this.userService = userService;
    }

    public String generateToken(user user){

    }


}
