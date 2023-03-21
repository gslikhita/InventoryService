package com.olx.inventoryManagementSystem.controller;

import com.olx.inventoryManagementSystem.controller.dto.LoginResponse;
import com.olx.inventoryManagementSystem.controller.dto.UserRequest;
import com.olx.inventoryManagementSystem.repository.JPAUserRepository;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import com.olx.inventoryManagementSystem.service.LoginUserService;
import com.olx.inventoryManagementSystem.service.UserService;
import com.olx.inventoryManagementSystem.utils.JwtUtil;
import com.olx.inventoryManagementSystem.utils.LoadByUsername;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean({UserService.class})
@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @MockBean
    UserService userService;

    @MockBean
    LoginUserService loginUserService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    UserRepository userRepositoryBean;

    @MockBean
    JPAUserRepository jpaUserRepositoryBean;

    @MockBean
    LoadByUsername loadByUsername;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        UserController userController = new UserController(userService,loginUserService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void ShouldReturnRegistrationResponse() throws Exception {
        UserRequest userRequest = new UserRequest("parimalvarma@gmail.com", "vparimal587");
        when(userService.createUser(userRequest)).thenReturn("parimalvarma@gmail.com");

        MockHttpServletRequestBuilder postRequest = post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"parimalvarma@gmail.com\",\"password\":\"vparimal587\"}");
        this.mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
    }

    @Test
    void ShouldReturnLoginResponse() throws Exception {
        UserDetails dummy = new org.springframework.security.core.userdetails.User("user@email.com", "123456", new ArrayList<>());
        LoginResponse loginResponse = new LoginResponse(jwtUtil.generateToken(dummy));
        when(loginUserService.createAuthenticationToken(new UserRequest("parimalvarma@gmail.com", "vparimal587"))).thenReturn(loginResponse);

        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"parimalvarma@gmail.com\",\"password\":\"vparimal587\"}");
        this.mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
    }

}
