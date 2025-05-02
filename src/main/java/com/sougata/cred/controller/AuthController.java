package com.sougata.cred.controller;

import com.sougata.cred.service.AuthService;
import com.sougata.cred.service.EncryptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EncryptionService encryptionService;

    public AuthController(AuthService authService, EncryptionService encryptionService) {
        this.authService = authService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> payload) {
        String token = authService.register(payload.get("username"), payload.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload) {
        String token = authService.login(payload.get("username"), payload.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @DeleteMapping("/delete-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAccount() {
        String username = encryptionService.getCurrentUsername();
        authService.deleteAccount(username);
        return ResponseEntity.noContent().build();
    }

}
