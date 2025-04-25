package com.sougata.cred.service;

import com.sougata.cred.cache.InMemoryKeyCache;
import com.sougata.cred.model.UserEntity;
import com.sougata.cred.repository.UserRepository;
import com.sougata.cred.util.EncryptionUtil;
import com.sougata.cred.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.sougata.cred.util.EncryptionUtil.generateSalt;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final InMemoryKeyCache inMemoryKeyCache;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder, JwtUtil jwtUtil, InMemoryKeyCache inMemoryKeyCache) {
        this.userRepo = userRepo;
        this.passwordEncoder = encoder;
        this.jwtUtil = jwtUtil;
        this.inMemoryKeyCache = inMemoryKeyCache;
    }

    public String register(String username, String rawPassword) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(rawPassword));
        userEntity.setEncryptionSalt(generateSalt());

        userRepo.save(userEntity);

        return jwtUtil.generateToken(username);
    }

    public String login(String username, String rawPassword) {
        UserEntity userEntity = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, userEntity.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        SecretKey key = null;

        try {
            // Derive AES key using PBKDF2 with HmacSHA256
            key = EncryptionUtil.deriveKey(
                    rawPassword, // entered password
                    userEntity.getEncryptionSalt()
            );
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Error deriving AES key using PBKDF2 with HmacSHA256: {}", e.getStackTrace());
        }

        // Save to a session-store (e.g., ConcurrentHashMap<username, SecretKey>)
        inMemoryKeyCache.store(userEntity.getUsername(), key);

        return jwtUtil.generateToken(username);
    }
}
