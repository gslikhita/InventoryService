package com.olx.inventoryManagementSystem.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final String SECRET_KEY = "InventoryManagementSystem@123";

    private static UserDetails dummy;

    private static String jwtToken;

    JwtUtil jwtUtil;

    @BeforeEach
    void init() {
        jwtUtil = new JwtUtil();
    }

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        dummy = new org.springframework.security.core.userdetails.User("user@email.com", "vparimal587", new ArrayList<>());
        jwtToken = jwtUtil.generateToken(dummy);
    }

    @Test
    void ShouldReturnEmailWhenTokenIsGiven() {
        String expectedEmail = "user@email.com";

        String actualEmail = jwtUtil.extractEmail(jwtToken);

        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    void ShouldReturnGeneratedToken() {
        String actualToken = jwtUtil.generateToken(dummy);
        Claims claims = Jwts.parser()
                            .setSigningKey(SECRET_KEY)
                            .parseClaimsJws(actualToken)
                            .getBody();
        String email = claims.getSubject();

        assertFalse(actualToken.isEmpty());
        assertEquals(email,"user@email.com");
    }
}
