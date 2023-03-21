package com.olx.inventoryManagementSystem.service;

import com.olx.inventoryManagementSystem.controller.dto.LoginResponse;
import com.olx.inventoryManagementSystem.controller.dto.UserRequest;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import com.olx.inventoryManagementSystem.utils.JwtUtil;
import com.olx.inventoryManagementSystem.utils.LoadByUsername;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class LoginUserServiceTest {

    private static UserDetails dummy;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtUtil jwtUtil = new JwtUtil();

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    LoadByUsername loadByUsername;

    private LoginUserService loginUserService;

    @BeforeEach
    void setup() {
        loginUserService = new LoginUserService(userRepository, jwtUtil, loadByUsername, authenticationManager);
        dummy = new org.springframework.security.core.userdetails.User("user@email.com", "123456", new ArrayList<>());
    }

    @Test
    public void ShouldReturnLoginResponse() throws Exception {
        LoginResponse expectedResponse = new LoginResponse("124");
        UserRequest userRequest = new UserRequest("user@email.com", "123456");
        when(loadByUsername.loadUserByUsername("user@email.com")).thenReturn(dummy);
        when(jwtUtil.generateToken(dummy)).thenReturn("124");

        LoginResponse actualResponse = loginUserService.createAuthenticationToken(userRequest);

        assertEquals(expectedResponse, actualResponse);
        verify(jwtUtil, times(1)).generateToken(dummy);
        verify(loadByUsername, times(1)).loadUserByUsername("user@email.com");
    }

    @Test
    public void ShouldThrowInvalidLoginCredentialException() {
        String expectedResponse = "Invalid Login Credential";
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken("user@email.com", "hello");
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenThrow(new BadCredentialsException(""));

        Exception actualError = Assertions.assertThrows(Exception.class, () -> loginUserService.createAuthenticationToken(new UserRequest("user@email.com", "hello")));

        assertEquals(expectedResponse, actualError.getMessage());
    }

}