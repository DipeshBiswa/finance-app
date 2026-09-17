package com.financeapp.finance_app.config;

import com.financeapp.finance_app.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String requestUri = request.getRequestURI();
        
        logger.info("JwtFilter processing: " + request.getMethod() + " " + requestUri);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.info("Authorization header found, extracting token...");
            String token = authHeader.substring(7);

            try {
                String username = jwtService.extractUsername(token);
                logger.info("Username extracted from token: " + username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtService.isValidToken(token, username)) {
                        logger.info("Token is valid for user: " + username);
                        var userDetails = userDetailsService.loadUserByUsername(username);
                        var authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        logger.info("✓ Security context set for user: " + username);
                    } else {
                        logger.warn("✗ Token invalid or expired for user: " + username);
                    }
                }
            } catch (Exception e) {
                logger.debug("JWT authentication failed");
            }
        } else {
            logger.debug("No valid Authorization header for: " + requestUri);
        }

        filterChain.doFilter(request, response);
    }
}
