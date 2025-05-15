package com.example.springjwt.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
//Redis 저장 조회 삭제 가능하게
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        log.info("RedisUtil 초기화됨: RedisTemplate = {}", redisTemplate);
    }

    public void saveRefreshToken(String username, String refreshToken) {
        log.info("Redis 저장 시도 - user: {}, token: {}", username, refreshToken);
        redisTemplate.opsForValue().set("refresh:" + username, refreshToken);
    }

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("refresh:" + username);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh:" + username);
    }

}
