package com.financeapp.finance_app.service.Tools;

import org.springframework.stereotype.Component;

import com.financeapp.finance_app.service.UserService;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;

@Component
public class UserDataTool{
    private UserService userService;

    public UserDataTool(UserService userService){
        this.userService = userService;
    }

    @Tool("gets the user's name and email")
    public String getUserData(@ToolMemoryId Long userId){
        var user = userService.findById(userId).orElseThrow();
        return "Username: " + user.getUsername() + ", email: " + user.getEmail();
    }
}
