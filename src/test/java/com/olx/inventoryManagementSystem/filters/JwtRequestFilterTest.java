package com.olx.inventoryManagementSystem.filters;

import com.olx.inventoryManagementSystem.exceptions.ForbiddenRequestException;
import com.olx.inventoryManagementSystem.exceptions.InvalidTokenException;
import com.olx.inventoryManagementSystem.exceptions.TokenExpiredException;
import com.olx.inventoryManagementSystem.repository.JPAUserRepository;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import com.olx.inventoryManagementSystem.security.WebSecurityConfig;
import com.olx.inventoryManagementSystem.service.LoginUserService;
import com.olx.inventoryManagementSystem.utils.JwtUtil;
import com.olx.inventoryManagementSystem.utils.LoadByUsername;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class JwtRequestFilterTest {

    private static String jwtToken;

    private static UserDetails dummy;

    JwtRequestFilter jwtRequestFilter;


    @Mock
    UserRepository userRepository;

    @Mock
    JwtUtil jwtUtil = new JwtUtil();

    @Mock
    WebSecurityConfig webSecurityConfig;

    @Mock
    LoadByUsername loadByUsername;

    @Mock
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtRequestFilter = new JwtRequestFilter(userRepository, loadByUsername, jwtUtil, resolver);
        dummy = new org.springframework.security.core.userdetails.User("user@email.com", "vparimal587", new ArrayList<>());
        jwtToken = jwtUtil.generateToken(dummy);
    }

    @Test
    void WhenAuthorizationHeaderIsPresent() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.addHeader("Authorization", "Bearer " + jwtToken);
        when(loadByUsername.loadUserByUsername("user@email.com")).thenReturn(dummy);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(loadByUsername, times(1)).loadUserByUsername("user@email.com");
    }

    @Test
    void ShouldThrowRuntimeExceptionWhenTokenNotStartWithBearer() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.addHeader("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImV4cCI6MTY3NzY4NzY1MSwiaWF0IjoxNjc3NjY5NjUxfQ.2UaKNDmUbgtnfdYI3WzTY4RjcboZJM9LOdGMYQqD95");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(resolver, times(1)).resolveException(request, response, null, new InvalidTokenException("Token is Invalid"));
    }

    @Test
    void ShouldResolveInvalidTokenException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImV4cCI6MTY3NzY4NzY1MSwiaWF0IjoxNjc3NjY5NjUxfQ.2UaKNDmUbgtnfdYI3WzTY4RjcboZJM9LOdGMYQqD95");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(resolver, times(1)).resolveException(request, response, null, new InvalidTokenException("Token is Invalid"));
    }

    @Test
    void ShouldResolveTokenExpiredException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTdW1hMUBnbWFpbC5jb20iLCJleHAiOjE2Nzg4NzM5MjIsImlhdCI6MTY3ODg3MzkyMX0.0SJQ7Jyg9v-XNWxZ7zrOZ4TleTCUi_7SbILOcnfKj2o");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(resolver, times(1)).resolveException(request, response, null, new TokenExpiredException("Token is expired"));
    }

    @Test
    void WhenAuthorizationHeaderIsNullAndIsPermittedForLogin() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.setRequestURI("/users/login");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);
    }

    @Test
    void WhenAuthorizationHeaderIsNullAndIsPermittedForRegister() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.setRequestURI("/users/register");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);
    }

    @Test
    void ShouldResolveForbiddenRequestException() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.setMethod("POST");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(resolver, times(1)).resolveException(request, response, null, new ForbiddenRequestException("Forbidden Request"));
    }
}
