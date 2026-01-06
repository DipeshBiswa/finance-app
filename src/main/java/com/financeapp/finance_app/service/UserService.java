package com.financeapp.finance_app.service;

import com.financeapp.finance_app.exceptions.EmailAlreadyExistsException;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public user createUser(String username, String password, String email){
        //hash password before adding user
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("Email already exits");
        }
        String hashPassword = hashPassword(password);
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

    public String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(byte b: hashBytes){
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm no found",e);
        }
    }
}
