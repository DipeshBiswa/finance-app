package com.financeapp.finance_app.service;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.repository.TransactionRepository;
import com.financeapp.finance_app.repository.UserRepository;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

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
    public String exchangePublicToken(String publicToken, String username) throws IOException {
        ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest().publicToken(publicToken);
        Response<ItemPublicTokenExchangeResponse> response = apiClient.itemPublicTokenExchange(request).execute();

        if(response.isSuccessful()){
            ItemPublicTokenExchangeResponse body = response.body();
            user user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user not found: "+ username));
            user.setPlaidAccessToken(body.getAccessToken());
            user.setPlaidItemId(body.getItemId());
            userRepository.save(user);
            return "Bank successfully linked to user: " + username;
        }else{
            throw new RuntimeException("Plaid Exchange Error: "+ response.errorBody().string());
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
    public void syncTransactions(String username)throws Exception{
        user user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user not found: "+ username));
        String cursor = user.getPlaidCursor();
        boolean hasMore = true;

        while(hasMore){
            TransactionsSyncRequest request = new TransactionsSyncRequest()
                    .accessToken(user.getPlaidAccessToken())
                    .cursor(cursor)
                    .count(500);

            Response<TransactionsSyncResponse> response = apiClient.transactionsSync(request).execute();
            if(!response.isSuccessful()){
                throw new RuntimeException("Plaid Sync Failed: " +  response.errorBody().string());
            }
            TransactionsSyncResponse data = response.body();

            for(com.plaid.client.model.Transaction plaidTx: data.getAdded()){
                if(transactionRepository.findByPlaidTransactionId(plaidTx.getTransactionId()).isEmpty()){
                    Transaction transaction = convertToTransaction(plaidTx, user);
                    transactionRepository.save(transaction);
                }
            }
            for(com.plaid.client.model.Transaction plaidTx: data.getModified()){
                transactionRepository.findByPlaidTransactionId(plaidTx.getTransactionId())
                        .ifPresent(existing -> {
                            existing.setAmount(BigDecimal.valueOf(plaidTx.getAmount()));
                            transactionRepository.save(existing);
                        });
            }
            for(RemovedTransaction removedTx: data.getRemoved()){
                transactionRepository.findByPlaidTransactionId(removedTx.getTransactionId())
                        .ifPresent(transactionRepository::delete);
            }
            cursor = data.getNextCursor();
            hasMore = data.getHasMore();

            user.setPlaidCursor(cursor);
            userRepository.save(user);

        }
    }
}
