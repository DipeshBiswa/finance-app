package com.financeapp.finance_app.controller;

import com.financeapp.finance_app.service.PlaidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/plaid")
public class PlaidController {
    @Autowired
    private PlaidService plaidService;

    public PlaidController(PlaidService plaidService){
        this.plaidService = plaidService;
    }
    @PostMapping("/create-link-token")
    public ResponseEntity<?> createLinkToken(Principal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).body("Error: You must be logged in.");
        }
        // Now you know for sure principal.getName() won't crash
        String linkToken = plaidService.createLinkToken(principal.getName());
        return ResponseEntity.ok(Map.of("linkToken", linkToken));
    }
    @PostMapping("/exchange-public-token")
    public ResponseEntity<?> exchangeToken(@RequestBody Map<String, String> payload, Principal principal)throws Exception{
        String publicToken = payload.get("publicToken");
        if(publicToken == null){
            return ResponseEntity.badRequest().body("publicToken is missing");
        }
        String exhangedToken = plaidService.exchangePublicToken(publicToken, principal.getName());
        return ResponseEntity.ok().body(Map.of(exhangedToken, "Bank account linked successfully"));
    }
    @PostMapping("/sync")
    public ResponseEntity<?> triggerSync(Principal principal) throws Exception{
        try{
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("error: No logged-in user found.");
            }
            plaidService.syncTransactions(principal.getName());
            return ResponseEntity.ok().body(Map.of("message", "Sync completed"));
        }catch(Exception e){
            return ResponseEntity.badRequest().body("error: " + e.getMessage());
        }
    }

}
