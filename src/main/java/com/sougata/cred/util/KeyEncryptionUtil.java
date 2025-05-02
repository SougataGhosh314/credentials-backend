package com.sougata.cred.util;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public final class KeyEncryptionUtil {
    private static final String MASTER_KEY = System.getenv("MASTER_KEY"); // Set securely via env
    private static final SecretKey MASTER_SECRET_KEY = AesEncryptor.generateKeyFromPassword(MASTER_KEY, "server-salt");

    public static String encryptUserKey(SecretKey key) {
        return AesEncryptor.encrypt(Base64.getEncoder().encodeToString(key.getEncoded()), MASTER_SECRET_KEY);
    }

    public static SecretKey decryptUserKey(String encrypted) {
        String decoded = AesEncryptor.decrypt(encrypted, MASTER_SECRET_KEY);
        byte[] keyBytes = Base64.getDecoder().decode(decoded);
        return new SecretKeySpec(keyBytes, "AES");
    }
}
