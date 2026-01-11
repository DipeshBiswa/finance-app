package com.financeapp.finance_app.service;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class PlaidService {

    @Autowired
    private PlaidApi apiClient;

    public String createLinkToken(String clientUserId) throws Exception {
        LinkTokenCreateRequestUser user = new LinkTokenCreateRequestUser().clientUserId(clientUserId);
        LinkTokenCreateRequest request = new LinkTokenCreateRequest().user(user).clientName("Finance APP").products(Arrays.asList(Products.TRANSACTIONS, Products.AUTH))
                .countryCodes(Arrays.asList(CountryCode.US)).language("en");

        Response<LinkTokenCreateResponse> response = apiClient.linkTokenCreate(request).execute();
        if(response.isSuccessful() && response.body() != null){
            return response.body().getLinkToken();
        }else{
            throw new RuntimeException("Failed to create link token: "+ response.errorBody().string());
        }

    }
    public String exchangePublicToken(String publicToken) throws IOException {
        ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest().publicToken(publicToken);
        Response<ItemPublicTokenExchangeResponse> response = apiClient.itemPublicTokenExchange(request).execute();
        if(response.isSuccessful() && response.body() != null){
            return response.body().getAccessToken();
        }else{
            throw new RuntimeException("Failed to exchange token");
        }
    }
    public Transaction convertToTransaction(com.plaid.client.model.Transaction transaction, user user) {
        com.financeapp.finance_app.model.Transaction entity = new com.financeapp.finance_app.model.Transaction();
        entity.setDescription(transaction.getOriginalDescription());
        entity.setDate(transaction.getDate().atStartOfDay());
        entity.setAmount(BigDecimal.valueOf(transaction.getAmount()));

        String plaidCategory = transaction.getPersonalFinanceCategory().getPrimary();
        entity.setCatagory(Catagory.fromPlaid(plaidCategory));
        entity.setPlaidTransactionId(transaction.getTransactionId());
        entity.setUser(user);
        return entity;
    }
}
