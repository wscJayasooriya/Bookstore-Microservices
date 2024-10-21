package com.practical.bookService.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class RoleCheckAspect {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    public RoleCheckAspect(HttpServletRequest request) {
        this.request = request;
        this.objectMapper = new ObjectMapper(); // Initialize the ObjectMapper for JSON parsing
    }

    @Before("@annotation(roleAllowed)")
    public void checkRole(JoinPoint joinPoint, RoleAllowed roleAllowed) {
        // Extract the JWT from the request header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Decode JWT token and extract roles
            List<String> roles = getRolesFromToken(token);

            // Check if the user has any of the required roles
            List<String> allowedRoles = Arrays.asList(roleAllowed.value());
            if (!roles.stream().anyMatch(allowedRoles::contains)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization Header Missing or Invalid");
        }
    }

    private List<String> getRolesFromToken(String token) {
        // Split the token into its components
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }

        // Decode the payload part (the second part)
        String payload = parts[1];
        String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));

        try {
            // Parse the JSON to extract roles
            JsonNode jsonNode = objectMapper.readTree(decodedPayload);
            JsonNode rolesNode = jsonNode.get("roles");

            if (rolesNode != null && rolesNode.isArray()) {
                return objectMapper.convertValue(rolesNode, List.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT payload", e);
        }

        return Arrays.asList(); // Return an empty list if no roles are found
    }
}
