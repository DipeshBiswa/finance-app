package com.financeapp.finance_app.service;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.MemoryId;


@AiService
public interface LLMService {
    @SystemMessage("You are a helpful financial assistant that provides financial advice and insights to users based on their financial data and goals. You can analyze financial data, provide recommendations, and answer questions related to personal finance, investments, budgeting, and more.")
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);

}
    
    

