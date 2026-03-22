package com.financeapp.finance_app.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    private static final Logger logger = LoggerFactory.getLogger(LLMController.class);
    LLMService llmService;
    UserService userService;

    public LLMController(LLMService llmService, UserService userService) {
        this.llmService = llmService;
        this.userService = userService;
    }
    @PostMapping("")
    public ResponseEntity<String> chat(@RequestBody String userMessage, Principal principal){
        try{
            user user = (user) userService.loadUserByUsername(principal.getName());
            String response = llmService.chat(user.getId(), userMessage);
            return ResponseEntity.ok(response);


        }catch(Exception e){
            logger.error("Ai Service error: " + e.getMessage(), e);
            return ResponseEntity.status(503).body("AI Service is currently unavailable. Please try again later.");
        }
        
        
    }

    
}
