package com.olx.inventoryManagementSystem.service;

import com.olx.inventoryManagementSystem.controller.dto.UserRequest;
import com.olx.inventoryManagementSystem.exceptions.UserAlreadyExistsException;
import com.olx.inventoryManagementSystem.model.User;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import com.olx.inventoryManagementSystem.security.WebSecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    UserService userService;

    @Mock
    WebSecurityConfig webSecurityConfig;

    @Captor
    ArgumentCaptor<User> UserCaptor;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository, webSecurityConfig);
    }

    @Test
    public void ShouldThrowUserAlreadyException() {
        String email = "user@email.com";
        User user = new User(email, "vparimal587");
        when(userRepository.ExistByEmail(email)).thenReturn(Optional.of(user));
        String expectedResponse = "User Already Exists";

        Exception actualError = Assertions.assertThrows(Exception.class, () -> userService.createUser(new UserRequest(email, "vparimal587")));

        assertEquals(expectedResponse, actualError.getMessage());
        verify(userRepository, times(1)).ExistByEmail(email);
    }

    @Test
    public void ShouldReturnEmailWhenUserIsCreated() throws UserAlreadyExistsException {
        String email = "user@email.com";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<User> user=Optional.empty();
        when(userRepository.ExistByEmail(email)).thenReturn(user);
        when(webSecurityConfig.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
        when(userRepository.create(UserCaptor.capture())).thenReturn(email);

        String actualEmail = userService.createUser(new UserRequest(email, "vparimal587"));

        assertEquals(email, actualEmail);
        User UserCaptorValue = UserCaptor.getValue();
        assertEquals(email,UserCaptorValue.getEmail());
        assertTrue(encoder.matches("vparimal587",UserCaptorValue.getPassword()));
        verify(userRepository, times(1)).ExistByEmail(email);
        verify(webSecurityConfig, times(1)).passwordEncoder();
    }

}
