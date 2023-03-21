package com.olx.inventoryManagementSystem.filters;

import com.olx.inventoryManagementSystem.exceptions.ForbiddenRequestException;
import com.olx.inventoryManagementSystem.exceptions.InvalidTokenException;
import com.olx.inventoryManagementSystem.exceptions.TokenExpiredException;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import com.olx.inventoryManagementSystem.utils.JwtUtil;
import com.olx.inventoryManagementSystem.utils.LoadByUsername;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN_IS_INVALID = "Token is Invalid";
    public static final String FORBIDDEN_REQUEST = "Forbidden Request";
    public static final String TOKEN_IS_EXPIRED = "Token is expired";
    private final static String BEARER = "Bearer ";
    private static final String[] PERMITTED_URI = new String[]{"/users/register", "/users/login", "swagger-ui", "api-docs"};

    UserRepository userRepository;

    JwtUtil jwtUtil;

    LoadByUsername loadByUsername;

    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    public JwtRequestFilter(UserRepository userRepository, LoadByUsername loadByUsername, JwtUtil jwtUtil, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.loadByUsername = loadByUsername;
        this.resolver = resolver;
    }

    private static String getHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isPermitted(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (isRequestPermittedWithNoAuthorizationHeader(request, response, filterChain)) return;
        if (!getHeader(request).startsWith(BEARER)) {
            resolver.resolveException(request, response, null, new InvalidTokenException(TOKEN_IS_INVALID));
            return;
        }

        String email = getEmail(getHeader(request).substring(7), request, response);
        if (email == null) {
            return;
        }
        validateToken(request, email);
        filterChain.doFilter(request, response);
    }

    private void validateToken(HttpServletRequest request, String email) {
        UserDetails userDetails = this.loadByUsername.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    private String getEmail(String jwt, HttpServletRequest request, HttpServletResponse response) {
        String email = null;
        try {
            email = jwtUtil.extractEmail(jwt);
        } catch (ExpiredJwtException e) {
            resolver.resolveException(request, response, null, new TokenExpiredException(TOKEN_IS_EXPIRED));
        } catch (SignatureException e) {
            resolver.resolveException(request, response, null, new InvalidTokenException(TOKEN_IS_INVALID));
        } catch (MalformedJwtException e) {
            resolver.resolveException(request, response, null, new TokenExpiredException(TOKEN_IS_INVALID));
        } catch (Exception e) {
            e.printStackTrace();
            resolver.resolveException(request, response, null, new Exception(e.getMessage()));
        }
        return email;
    }

    private boolean isRequestPermittedWithNoAuthorizationHeader(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (getHeader(request) != null) {
            return false;
        }
        if (!isPermitted(request)) {
            resolver.resolveException(request, response, null, new ForbiddenRequestException(FORBIDDEN_REQUEST));
            return true;
        }
        filterChain.doFilter(request, response);
        return true;
    }

    private boolean isPermitted(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        for (String uri : PERMITTED_URI) {
            if (requestURI.contains(uri)) {
                return true;
            }
        }
        return false;
    }

}