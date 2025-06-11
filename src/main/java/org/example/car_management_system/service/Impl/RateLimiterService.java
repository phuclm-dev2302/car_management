package org.example.car_management_system.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

private final RedisTemplate<String, Integer> redisTemplate;
private final int MAX_ATTEMPTS = 50;
private final Duration TIME = Duration.ofSeconds(30); // ví dụ: 50 lsn/ 30 giay

public void recordFailedAttempt(String key) { // key = username
    ValueOperations<String, Integer> ops = redisTemplate.opsForValue();
    Integer value = ops.get(key);
    if (value == null) {
        ops.set(key, 1, TIME);
    } else {
        int updated = ops.increment(key).intValue();
        redisTemplate.expire(key, TIME); // reset lại TTL
        log.warn("Failed attempt #{} for key: {}", updated, key);
    }
}

public boolean isBlocked(String key) {
    Integer count = redisTemplate.opsForValue().get(key);
    boolean blocked = count != null && count >= MAX_ATTEMPTS;
    log.warn("RateLimiter check: {} has {} attempts -> blocked = {}", key, count, blocked);
    return blocked;
}
public void resetAttempts(String key) {
    redisTemplate.delete(key);
}
}

