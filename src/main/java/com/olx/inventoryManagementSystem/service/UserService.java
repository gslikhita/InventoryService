package com.olx.inventoryManagementSystem.service;

import com.olx.inventoryManagementSystem.controller.dto.UserRequest;
import com.olx.inventoryManagementSystem.exceptions.UserAlreadyExistsException;
import com.olx.inventoryManagementSystem.model.User;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import com.olx.inventoryManagementSystem.security.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public static final String USER_ALREADY_EXISTS = "User Already Exists";
    UserRepository userRepository;
    WebSecurityConfig webSecurityConfig;

    @Autowired
    public UserService(UserRepository userRepository, WebSecurityConfig webSecurityConfig) {
        this.userRepository = userRepository;
        this.webSecurityConfig = webSecurityConfig;
    }

    public String createUser(UserRequest userRequest) throws UserAlreadyExistsException {
        if (!userRepository.ExistByEmail(userRequest.getEmail()).isEmpty()) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS);
        }
        User user = new User(userRequest.getEmail(), (webSecurityConfig.passwordEncoder().encode(userRequest.getPassword())));
        return this.userRepository.create(user);
    }
}
