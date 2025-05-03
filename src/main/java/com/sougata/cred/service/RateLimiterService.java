package com.sougata.cred.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    @Value("${rate.limiter.enabled:tree}")
    private boolean rateLimiterEnabled;

    private static final long TIME_WINDOW_MILLIS = 60000L; // 1 minute
    private static final int MAX_ATTEMPTS = 5;

    private final Map<String, RequestRecord> requestCounts = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        if (!rateLimiterEnabled)
            return true;

        RequestRecord record = requestCounts.computeIfAbsent(key, k -> new RequestRecord());

        synchronized (record) {
            long now = Instant.now().toEpochMilli();

            if (now - record.timestamp > TIME_WINDOW_MILLIS) {
                record.timestamp = now;
                record.count = 1;
                return true;
            }

            if (record.count < MAX_ATTEMPTS) {
                record.count++;
                return true;
            }

            return false;
        }
    }

    private static class RequestRecord {
        long timestamp = Instant.now().toEpochMilli();
        int count = 0;
    }
}
