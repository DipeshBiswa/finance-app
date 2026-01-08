package com.financeapp.finance_app.service;

import com.financeapp.finance_app.exceptions.EmailAlreadyExistsException;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;

public class UserServiceTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp(){
        bCryptPasswordEncoder = org.mockito.Mockito.mock(BCryptPasswordEncoder.class);
        userRepository = org.mockito.Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository, bCryptPasswordEncoder);
    }
    @Test
    void testRegisterUser_Success(){
        String rawPassword = "password";
        String rawUsername = "username";
        String rawEmail = "email";
        org.mockito.Mockito.when(userRepository.existsByEmail(rawEmail)).thenReturn(false);
        org.mockito.Mockito.when(userRepository.save(org.mockito.Mockito.any(user.class))).thenAnswer(invocation -> invocation.getArgument(0));
        org.mockito.Mockito.when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashed_password");
        user savedUser = userService.createUser(rawUsername, rawPassword, rawEmail);

        org.junit.jupiter.api.Assertions.assertNotNull(savedUser);
        org.junit.jupiter.api.Assertions.assertEquals(rawEmail,  savedUser.getEmail());

        org.junit.jupiter.api.Assertions.assertNotEquals(rawPassword, savedUser.getPassword());
        org.mockito.Mockito.verify(userRepository).save(org.mockito.Mockito.any(user.class));
    }
    @Test
    void testRegisterUser_Fail(){
        String rawPassword = "password";
        String rawUsername = "username";
        String rawEmail = "email";
        org.mockito.Mockito.when(userRepository.existsByEmail(rawEmail)).thenReturn(true);
        org.junit.jupiter.api.Assertions.assertThrows(EmailAlreadyExistsException.class,() ->{userService.createUser(rawUsername, rawPassword,rawEmail);});
        org.mockito.Mockito.verify(userRepository, org.mockito.Mockito.never()).save(org.mockito.Mockito.any(user.class));
    }
}
