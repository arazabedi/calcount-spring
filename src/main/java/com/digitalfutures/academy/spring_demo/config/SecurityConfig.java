package com.digitalfutures.academy.spring_demo.config;

import com.digitalfutures.academy.spring_demo.security.JwtAuthenticationFilter;
import com.digitalfutures.academy.spring_demo.service.UserService;
import com.digitalfutures.academy.spring_demo.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Annotates this class as a producer of beans
@EnableWebSecurity // Enables Spring Security
public class SecurityConfig {

    // Creates a password encoder bean for password hashing and verification
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt with a strength of 12 (higher is slower but more secure)
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        return new JwtAuthenticationFilter(jwtUtil, userService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Make sure CSRF is completely disabled
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Allow all auth endpoints
                        .requestMatchers("/api/auth/login").permitAll()  // Explicitly permit login
                        .requestMatchers("/api/auth/register").permitAll()  // Explicitly permit register
                        .anyRequest().authenticated())  // All other requests need authentication
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}