package com.financeapp.finance_app.config;

import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.plaid.client.ApiClient;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class PlaidConfig {
    @Value("${plaid.client.id}")
    private String clientId;
    @Value("${plaid.secret}")
    private String secret;
    @Value("${plaid.env:sandbox}")
    private String environment;

    @Bean
    public PlaidApi plaidClient() {
        Map<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", clientId);
        apiKeys.put("secret", secret);
        apiKeys.put("plaidVersion", "2020-09-14");

        ApiClient apiClient = new ApiClient(apiKeys);

        apiClient.setPlaidAdapter(switch (environment.toLowerCase(java.util.Locale.ROOT)) {
            case "sandbox" -> ApiClient.Sandbox;
            case "production" -> ApiClient.Production;
            default -> throw new IllegalArgumentException("PLAID_ENV must be sandbox or production");
        });

        return apiClient.createService(PlaidApi.class);


    }
}
