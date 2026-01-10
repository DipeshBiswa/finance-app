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

    @Bean
    public PlaidApi plaidClient() {
        Map<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", clientId);
        apiKeys.put("secret", secret);
        apiKeys.put("plaidVersion", "2020-09-14");

        ApiClient apiClient = new ApiClient(apiKeys);

        apiClient.setPlaidAdapter(ApiClient.Sandbox);

        PlaidApi plaidClient = apiClient.createService(PlaidApi.class);
        return plaidClient;

    }
}
