package com.sougata.cred.service;

import com.sougata.cred.util.AesEncryptor;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class KeyEncryptionService {
    private static final String MASTER_KEY = System.getenv("MASTER_KEY"); // Set securely via env
    private static final SecretKey MASTER_SECRET_KEY = AesEncryptor.generateKeyFromPassword(MASTER_KEY, "server-salt");

    @PostConstruct
    void init() {
        if (MASTER_KEY == null || MASTER_KEY.isBlank()) {
            throw new IllegalStateException("MASTER_KEY is missing. Set the 'encryption.master-key' environment variable.");
        }
    }

    public String encryptUserKey(SecretKey key) {
        return AesEncryptor.encrypt(Base64.getEncoder().encodeToString(key.getEncoded()), MASTER_SECRET_KEY);
    }

    public SecretKey decryptUserKey(String encrypted) {
        String decoded = AesEncryptor.decrypt(encrypted, MASTER_SECRET_KEY);
        byte[] keyBytes = Base64.getDecoder().decode(decoded);
        return new SecretKeySpec(keyBytes, "AES");
    }
}
