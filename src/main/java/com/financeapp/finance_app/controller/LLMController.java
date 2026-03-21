package com.financeapp.finance_app.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.LLMService;
import com.financeapp.finance_app.service.UserService;

@RestController
@RequestMapping("/api/chat")
public class LLMController {
    LLMService llmService;
    UserService userService;

    public LLMController(LLMService llmService, UserService userService) {
        this.llmService = llmService;
        this.userService = userService;
    }
    @PostMapping("")
    public String chat(@RequestBody String userMessage, Principal principal){
        user user = (user) userService.loadUserByUsername(principal.getName());
        return llmService.chat(user.getId(), userMessage);
        
    }

    
}
