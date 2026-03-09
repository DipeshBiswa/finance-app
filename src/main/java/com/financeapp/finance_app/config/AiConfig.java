package com.financeapp.finance_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

@Configuration
public class AiConfig {
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        //keeps the last 10 messages in memory for each new conversation for unique user
        return memoryId -> MessageWindowChatMemory.withMaxMessages(10);
    }
    
}
