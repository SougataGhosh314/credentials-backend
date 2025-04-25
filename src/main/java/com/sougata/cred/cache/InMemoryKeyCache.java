package com.sougata.cred.cache;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryKeyCache {
    private final ConcurrentMap<String, SecretKey> cache = new ConcurrentHashMap<>();

    public void store(String username, SecretKey key) {
        cache.put(username, key);
    }

    public SecretKey get(String username) {
        return cache.get(username);
    }

    public void remove(String username) {
        cache.remove(username);
    }

    public void clear() {
        cache.clear();
    }
}
