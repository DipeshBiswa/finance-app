package com.financeapp.finance_app.service;

import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
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
}
