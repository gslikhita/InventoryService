package com.olx.inventoryManagementSystem.controller;

import com.olx.inventoryManagementSystem.controller.dto.LoginResponse;
import com.olx.inventoryManagementSystem.controller.dto.RegistrationResponse;
import com.olx.inventoryManagementSystem.controller.dto.UserRequest;
import com.olx.inventoryManagementSystem.exceptions.UserAlreadyExistsException;
import com.olx.inventoryManagementSystem.service.LoginUserService;
import com.olx.inventoryManagementSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {
    public static final String USER_SUCCESSFULLY_REGISTERED = "User Successfully Registered";
    UserService userService;
    private LoginUserService loginUserService;

    @Autowired
    public UserController(UserService userService, LoginUserService loginUserService) {
        this.userService = userService;
        this.loginUserService = loginUserService;
    }

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegistrationResponse> createUser(@Valid @RequestBody UserRequest userRequest) throws UserAlreadyExistsException {
        this.userService.createUser(userRequest);
        return new ResponseEntity<>(new RegistrationResponse(USER_SUCCESSFULLY_REGISTERED), HttpStatus.CREATED);
    }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginResponse> createAuthenticationToken(@Valid @RequestBody UserRequest userRequest) throws Exception{
        return new ResponseEntity<>(this.loginUserService.createAuthenticationToken(userRequest), HttpStatus.CREATED);
    }
}
