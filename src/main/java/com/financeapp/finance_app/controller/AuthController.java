package com.financeapp.finance_app.controller;

import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.JwtService;
import com.financeapp.finance_app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody user user){
        try{
            logger.info("Register request for username: " + user.getUsername());
            user newUser = userService.createUser(user.getUsername(), user.getPassword(), user.getEmail());
            if(newUser != null){
                logger.info("✓ User registered successfully: " + newUser.getUsername());
                return new ResponseEntity<>(newUser, HttpStatus.CREATED);
            }else{
                logger.warn("✗ Failed to create user");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            logger.error("✗ Registration error: " + e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            logger.info("=== LOGIN REQUEST ===");
            logger.info("Username: " + username);
            
            if (username == null || password == null) {
                logger.warn("✗ Username or password is null");
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password required"));
            }

            Optional<user> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                logger.warn("✗ User not found: " + username);
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            user foundUser = userOpt.get();
            logger.info("User found: " + foundUser.getUsername());
            logger.info("Checking password...");
            
            if (!passwordEncoder.matches(password, foundUser.getPassword())) {
                logger.warn("✗ Password mismatch for user: " + username);
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            logger.info("✓ Password matches!");
            String token = jwtService.generateToken(username);
            logger.info("✓✓✓ LOGIN SUCCESSFUL ✓✓✓");
            
            return ResponseEntity.ok(Map.of("token", token, "username", username));
        } catch (Exception e) {
            logger.error("✗ Login error: " + e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

}
