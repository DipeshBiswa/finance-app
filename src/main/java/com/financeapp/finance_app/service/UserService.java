package com.financeapp.finance_app.service;

import com.financeapp.finance_app.config.SecurityConfig;
import com.financeapp.finance_app.exceptions.EmailAlreadyExistsException;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public user createUser(String username, String password, String email){
        //hash password before adding user
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("Email already exits");
        }
        if(username.length() < 6){
            throw new IllegalArgumentException("Username too short");
        }
        if(password.length() < 8){
            throw new IllegalArgumentException("Password too short");
        }
        String hashPassword = passwordEncoder.encode(password);
        user newUser = new user(username,hashPassword,email);
        return userRepository.save(newUser);

    }
    public Optional<user> findById(Long id){
        return userRepository.findById(id);
    }
    public void deleteById(Long id){
        userRepository.deleteById(id);
    }
    public List<user> findAll(){
        return userRepository.findAll();
    }
    public String getUserUsername(Long id){
        Optional<user> user = userRepository.findById(id);
        if(user.isPresent()){
            return user.get().getUsername();
        }
        return null;
    }
    public String getUserEmail(Long id){
        Optional<user> user = userRepository.findById(id);
        if(user.isPresent()){
            return user.get().getEmail();
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username not found: " + username));
    }
}
