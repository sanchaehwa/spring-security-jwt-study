package com.example.springjwt.config;

import com.example.springjwt.jwt.JWTUtil;
import com.example.springjwt.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@RequiredArgsConstructor

public class FilterConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil; // RedisUtil을 주입

    @Bean
    public LoginFilter loginFilter() throws Exception {
        return new LoginFilter(
                authenticationConfiguration.getAuthenticationManager(),
                jwtUtil,
                redisUtil // 주입
        );
    }
}
