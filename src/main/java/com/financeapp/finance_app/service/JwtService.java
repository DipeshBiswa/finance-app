package com.financeapp.finance_app.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        byte[] byteArray = secretKey.getBytes();
        key = Keys.hmacShaKeyFor(byteArray);
        logger.info("✓ JwtService initialized with secret key");
    }

    Long expiration = 86400000L;
    
    public String generateToken(String username){
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
        logger.info("✓ Generated token for user: " + username);
        return token;
    }
    
    public String extractUsername(String token){
        try {
            String username = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            logger.info("✓ Extracted username from token: " + username);
            return username;
        } catch (Exception e) {
            logger.debug("JWT username extraction failed");
            throw e;
        }
    }
    
    private Date extractExpirationDate(String token){
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
        } catch (Exception e) {
            logger.debug("JWT expiration extraction failed");
            throw e;
        }
    }
    
    public boolean isTokenExpired(String token){
        try {
            Date expiration = extractExpirationDate(token);
            boolean expired = expiration.before(new Date());
            logger.info("Token expiration check: expired=" + expired);
            return expired;
        } catch (Exception e) {
            logger.debug("JWT expiration check failed");
            return true;
        }
    }
    
    public boolean isValidToken(String token, String username){
        try {
            String extractedUsername = extractUsername(token);
            boolean usernameMatch = username.equals(extractedUsername);
            boolean notExpired = !isTokenExpired(token);
            boolean valid = usernameMatch && notExpired;
            
            logger.info("Token validation: username_match=" + usernameMatch + ", not_expired=" + notExpired + ", valid=" + valid);
            return valid;
        } catch (Exception e) {
            logger.debug("JWT validation failed");
            return false;
        }
    }
}

