package com.financeapp.finance_app.config;

import com.financeapp.finance_app.service.LLMService;
import com.financeapp.finance_app.service.Tools.UserDataTool;
import com.financeapp.finance_app.service.Tools.UserGoalDataTool;
import com.financeapp.finance_app.service.Tools.UserTransactionDataTool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

@Configuration
public class AiConfig {
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        //keeps the last 10 messages in memory for each new conversation for unique user
        return memoryId -> MessageWindowChatMemory.withMaxMessages(10);
    }

    @Bean
    public LLMService llmService(
            @Value("${ai.gemini.api-key:}") String apiKey,
            @Value("${ai.gemini.model:gemini-2.5-flash}") String modelName,
            @Value("${ai.gemini.temperature:0.7}") double temperature,
            ChatMemoryProvider chatMemoryProvider,
            UserDataTool userDataTool,
            UserTransactionDataTool userTransactionDataTool,
            UserGoalDataTool userGoalDataTool) {
        if (!StringUtils.hasText(apiKey)) {
            return (memoryId, userMessage) -> {
                throw new IllegalStateException("AI chat is not configured");
            };
        }

        return AiServices.builder(LLMService.class)
                .chatLanguageModel(createChatModel(apiKey.trim(), modelName, temperature))
                .chatMemoryProvider(chatMemoryProvider)
                .tools(userDataTool, userTransactionDataTool, userGoalDataTool)
                .build();
    }

    ChatLanguageModel createChatModel(String apiKey, String modelName, double temperature) {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey.trim())
                .modelName(modelName)
                .temperature(temperature)
                .logRequestsAndResponses(false)
                .build();
    }
    
}
