package com.practical.identityService.controller;

import com.practical.identityService.dto.AssignRoleRequest;
import com.practical.identityService.dto.JwtResponse;
import com.practical.identityService.dto.LoginRequest;
import com.practical.identityService.dto.RegisterRequest;
import com.practical.identityService.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/identity")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRole(@RequestBody AssignRoleRequest request) {
        return ResponseEntity.ok(authService.assignRole(request));
    }
}