package com.digitalfutures.academy.spring_demo.security;

import com.digitalfutures.academy.spring_demo.model.User;
import com.digitalfutures.academy.spring_demo.service.UserService;
import com.digitalfutures.academy.spring_demo.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@AllArgsConstructor
// Extending OncePerRequestFilter invokes the filter once for every HTTP request barring explicitly skipped endpoints
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    // doFilterInternal is a protected abstract method in OncePerRequestFilter...
    // ... that we override in order to implement our own custom filter logic
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip authentication for auth endpoints
        if (request.getRequestURI().contains("/api/auth/")) {
            filterChain.doFilter(request, response) ;
            return;
        }

        // Get token from Authorization header
        String token = extractToken(request);

        if (token != null) {
            // Retrieve the username based on the JWT token
            String username = jwtUtil.extractUsername(token);

            // Check that the username exists and the token is valid
            if (username != null) {
                User user = userService.findByUsername(username);

                if (user != null && jwtUtil.validateToken(token, username)) {
                    // Set the user in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    user, null,
                                    Collections.singletonList(new SimpleGrantedAuthority("USER"))
                            )
                    );
                }
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // Retrieve the token from the Authorization header
        String bearer = request.getHeader("Authorization");
        // Check if the header is present and formatted correctly
        if (bearer != null && bearer.startsWith("Bearer ")) {
            // Return the token without the "Bearer " prefix
            return bearer.substring(7);
        }
        return null;
    }
}