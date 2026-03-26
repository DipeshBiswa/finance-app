package com.financeapp.finance_app.service.Tools;

import org.springframework.stereotype.Component;

import com.financeapp.finance_app.service.UserService;

import dev.langchain4j.agent.tool.Tool;

@Component
public class UserDataTool{
    private UserService userService;

    public UserDataTool(UserService userService){
        this.userService = userService;
    }

    @Tool("gets the user's name and email")
    public String getUserData(Long userId){
        return userService.findById(userId).orElseThrow().toString();
    }
    
}
