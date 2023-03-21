package com.olx.inventoryManagementSystem.utils;

import com.olx.inventoryManagementSystem.model.User;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadByUsernameTest {

    LoadByUsername loadByUsername;
    @Mock
    UserRepository userRepository;


    @BeforeEach
    void init() {
        loadByUsername = new LoadByUsername(userRepository);
    }

    @Test
    public void ShouldThrowUsernameNotFoundException(){
        String email = getEmail();
        String expectedResponse = email + " not found.";
        Optional<User> user=Optional.empty();
        when(userRepository.ExistByEmail(email)).thenReturn(user);

        Exception actualError = Assertions.assertThrows(Exception.class, () -> loadByUsername.loadUserByUsername(email));

        assertEquals(expectedResponse, actualError.getMessage());
    }

    @Test
    public void ShouldReturnUserDetails(){
        String email = getEmail();
        String password = "123456";
        UserDetails expectedResponse = new org.springframework.security.core.userdetails.User(email, password, new ArrayList<>());;
        when(userRepository.ExistByEmail(email)).thenReturn(Optional.of(new User(email, password)));

        UserDetails actualResponse = loadByUsername.loadUserByUsername(email);

        assertEquals(expectedResponse, actualResponse);
    }

    private static String getEmail() {
         return "user@email.com";
    }
}