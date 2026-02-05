package com.financeapp.finance_app.controller;

import com.financeapp.finance_app.service.PlaidService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> createLinkToken(Principal principal) {
        try {
            // If principal is null, use a fallback username for testing
            String username = (principal != null) ? principal.getName() : "test_user";
            System.out.println("DEBUG: Fetching token for: " + username);

            String linkToken = plaidService.createLinkToken(username);
            return ResponseEntity.ok(Map.of("linkToken", linkToken));
        } catch (Exception e) {
            e.printStackTrace(); // This prints the error to your IntelliJ console
            return ResponseEntity.status(500).body("Plaid Service Error: " + e.getMessage());
        }
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
            plaidService.syncTransactions(principal.getName());
            return ResponseEntity.ok().body(Map.of("message", "Sync completed"));
        }catch(Exception e){
            return ResponseEntity.badRequest().body("error: " + e.getMessage());
        }
    }

}
