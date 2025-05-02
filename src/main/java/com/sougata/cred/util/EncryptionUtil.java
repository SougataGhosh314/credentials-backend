package com.sougata.cred.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public final class EncryptionUtil {
    public static String generateSalt() {
        try {
            return Base64.getEncoder().encodeToString(SecureRandom.getInstanceStrong().generateSeed(16));
        } catch (NoSuchAlgorithmException e) {
            log.error("Exception occurred while generating salt: {}", (Object) e.getStackTrace());
            return "";
        }
    }

    public static SecretKey generateRandomAesKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // 128 or 192 if 256 not supported
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate AES key", e);
        }
    }
}
