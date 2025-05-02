package com.sougata.cred.service;

import com.sougata.cred.cache.InMemoryKeyCache;
import com.sougata.cred.model.UserEntity;
import com.sougata.cred.util.AesEncryptor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Slf4j
@Service
public class EncryptionService {

    private final InMemoryKeyCache keyCache;

    public EncryptionService(InMemoryKeyCache keyCache) {
        this.keyCache = keyCache;
    }

    public String encrypt(String plaintext) {
        String username = getCurrentUsername();
        SecretKey key = keyCache.get(username);
        if (key == null) {
            throw new IllegalStateException("Encryption key not found for user: " + username);
        }
        return AesEncryptor.encrypt(plaintext, key);
    }

    public String decrypt(String ciphertext) {
        String username = getCurrentUsername();
        SecretKey key = keyCache.get(username);
        if (key == null) {
            throw new AccessDeniedException("Your session expired. Please log in again.");
        }
        return AesEncryptor.decrypt(ciphertext, key);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserEntity user) {
            return user.getUsername();
        } else {
            throw new IllegalStateException("Principal is not a UserEntity");
        }
    }

    @PostConstruct
    public void init() {
        // optionally preload some test users/keys
    }

    @PreDestroy
    public void shutdown() {
        keyCache.clear();
    }
}
