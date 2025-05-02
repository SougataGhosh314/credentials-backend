package com.sougata.cred.service;

import com.sougata.cred.cache.InMemoryKeyCache;
import com.sougata.cred.model.UserEntity;
import com.sougata.cred.repository.UserRepository;
import com.sougata.cred.util.EncryptionUtil;
import com.sougata.cred.util.JwtUtil;
import com.sougata.cred.util.KeyEncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

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

        String salt = generateSalt();
        SecretKey aesKey = EncryptionUtil.generateRandomAesKey();
        String encryptedAesKey = KeyEncryptionUtil.encryptUserKey(aesKey);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(rawPassword));
        userEntity.setEncryptionSalt(salt);
        userEntity.setEncryptedAesKey(encryptedAesKey);

        userRepo.save(userEntity);

        // Cache the decrypted AES key for the user
        inMemoryKeyCache.store(username, aesKey);

        return jwtUtil.generateToken(username);
    }

    public String login(String username, String rawPassword) {
        UserEntity userEntity = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, userEntity.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Decrypt the user-specific AES key using server master key
        SecretKey aesKey = KeyEncryptionUtil.decryptUserKey(userEntity.getEncryptedAesKey());

        // Save to a session-store (e.g., ConcurrentHashMap<username, SecretKey>)
        inMemoryKeyCache.store(userEntity.getUsername(), aesKey);

        return jwtUtil.generateToken(username);
    }
}
