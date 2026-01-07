package com.financeapp.finance_app.service;

import com.financeapp.finance_app.model.user;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private String secretKey;
    private SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
        byte[] byteArray = secretKey.getBytes();
        key = Keys.hmacShaKeyFor(byteArray);
    }

    Long expiration = 86400000L;
    public String generateToken(String username){
        return Jwts.builder().subject(username).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + expiration)).signWith(key).compact();
    }
    public String extractUsername(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }


}
