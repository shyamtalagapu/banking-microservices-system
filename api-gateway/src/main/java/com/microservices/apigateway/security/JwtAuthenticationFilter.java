package com.microservices.apigateway.security;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Autowired
    private JwtUtility jwtUtil;
    private static final Logger LOGGER= LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login",
            "/auth/register",
            "/customers/saveCustomer",
            "/auth/internal/createUser"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        LOGGER.info("Path :"+path);

        // Allow public endpoints
        if (PUBLIC_ENDPOINTS.contains(path)) {
            return chain.filter(exchange);
        }

        // Check Authorization header (duplicate check removed)
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // FIX: Validate token FIRST before any claim extraction.
        // Previously, extractUserRole() was called before validateToken() with no
        // try-catch — a malformed/expired token caused parseClaimsJws() to throw
        // an unhandled exception, resulting in the 500 error.
        try {
            if (!jwtUtil.validateToken(token)) {
                return unauthorized(exchange, "Invalid or expired token");
            }
        } catch (Exception e) {
            return unauthorized(exchange, "Token validation failed: " + e.getMessage());
        }

        // Now safely extract role after token is confirmed valid
        try {
            String role = jwtUtil.extractUserRole(token);
            LOGGER.info("ROLE : "+role);
            //Only for CUSTOMER
            //checking the exact endpoints 
            if (path.startsWith("/accounts/create") || path.startsWith("/accounts/myAccounts")) {
                //Restricting the other roles to create or access the owned accounts
            	
            	if (!role.equals("ROLE_CUSTOMER")) {
                    return forbidden(exchange, "Customer access only");
                }
            }
            // Only for Employee
            //checking the exact endpoints 
            if (path.startsWith("/accounts/approve") || path.startsWith("/customers/all")) {
                if (!role.equals("ROLE_EMPLOYEE")) {
                    return forbidden(exchange, "Employee access only");
                }
            }
            //ADMIN
          //checking the exact endpoints 
            if (path.contains("/admin") || path.contains("/delete")) {
                if (!role.equals("ROLE_ADMIN")) {
                    return forbidden(exchange, "Admin access only");
                }
            }
            if (path.startsWith("/accounts/transferMoney")) {
                if (!role.equals("ROLE_CUSTOMER")) {
                    return forbidden(exchange, "Only customers allowed");
                }
            }
          
        } catch (Exception e) {
            return unauthorized(exchange, "Failed to extract token claims: " + e.getMessage());
        }

        return chain.filter(exchange);
    }

    // FIX: unauthorized() now writes a JSON body, consistent with forbidden()
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String body = """
            {
              "status": 401,
              "error": "Unauthorized",
              "message": "%s"
            }
            """.formatted(message);

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String body = """
            {
              "status": 403,
              "error": "Forbidden",
              "message": "%s"
            }
            """.formatted(message);

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}