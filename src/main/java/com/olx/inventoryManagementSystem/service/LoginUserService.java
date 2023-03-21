package com.olx.inventoryManagementSystem.service;

import com.olx.inventoryManagementSystem.controller.dto.LoginResponse;
import com.olx.inventoryManagementSystem.controller.dto.UserRequest;
import com.olx.inventoryManagementSystem.exceptions.InvalidLoginCredential;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import com.olx.inventoryManagementSystem.utils.JwtUtil;
import com.olx.inventoryManagementSystem.utils.LoadByUsername;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService{
    public static final String INVALID_LOGIN_CREDENTIAL = "Invalid Login Credential";
    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    LoadByUsername loadByUsername;
    private JwtUtil jwtUtil;

    @Autowired
    public LoginUserService(UserRepository userRepository, JwtUtil jwtUtil, LoadByUsername loadByUsername, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.loadByUsername = loadByUsername;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse createAuthenticationToken(UserRequest userRequest) throws Exception {
        authenticateUser(userRequest);
        return new LoginResponse(jwtUtil.generateToken(loadByUsername.loadUserByUsername(userRequest.getEmail())));
    }

    private void authenticateUser(UserRequest userRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getEmail(),
                    userRequest.getPassword()));
            System.out.println("error");
        } catch (AuthenticationException e) {
            throw new InvalidLoginCredential(INVALID_LOGIN_CREDENTIAL);
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
        System.out.println("error");
    }

}

