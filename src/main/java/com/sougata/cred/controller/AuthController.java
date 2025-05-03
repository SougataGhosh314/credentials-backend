package com.sougata.cred.controller;

import com.sougata.cred.service.AuthService;
import com.sougata.cred.service.EncryptionService;
import com.sougata.cred.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RateLimiterService rateLimiter;
    private final AuthService authService;
    private final EncryptionService encryptionService;

    public AuthController(RateLimiterService rateLimiter, AuthService authService, EncryptionService encryptionService) {
        this.rateLimiter = rateLimiter;
        this.authService = authService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> payload, HttpServletRequest httpRequest) {
        String clientIP = httpRequest.getRemoteAddr();

        if (!rateLimiter.isAllowed(clientIP + ":register")) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Too many login attempts. Try again later."));
        }

        String token = authService.register(payload.get("username"), payload.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload, HttpServletRequest httpRequest) {
        String clientIP = httpRequest.getRemoteAddr();

        if (!rateLimiter.isAllowed(clientIP + ":login")) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Too many login attempts. Try again later."));
        }

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
