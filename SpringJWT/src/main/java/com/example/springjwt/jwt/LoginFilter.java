package com.example.springjwt.jwt;

import com.example.springjwt.config.RedisUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RedisUtil redisUtil) {
        super.setAuthenticationManager(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //클라이언트 요청에서 사용자 Username , Password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        //Authentication Manager 가 검증을 할 수 있도록 값을 토큰에 담아 넘겨줌
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,null);

        return getAuthenticationManager().authenticate(authToken);
    }
    //로그인 성공  - JWT 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        log.info("Login Success - successfulAuthentication 실행됨");
        //유저 정보
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 600000L); //10분 만료시간
        String refresh = jwtUtil.createJwt("refresh", username, role, 1800000L); //30분 만료시간

        //access token 저장: 클라이언트 측.
        response.setHeader("access",access);
        //Redis에도 저장
        redisUtil.saveRefreshToken(username, refresh);
        //Refresh Token 쿠키에 저장
        response.addCookie(createCookie("refresh",refresh));
        response.setStatus(HttpStatus.OK.value());

    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

    //쿠키 생성
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        //XSS 공격으로부터 쿠키 보호(JS 으로 접근 불가능)
        cookie.setHttpOnly(true);
        //사이트 전체에서 쿠키 사용
        cookie.setPath("/");
        //쿠키의 유효기간 설정(2주)
        cookie.setMaxAge(14*24*60*60);
        return cookie;
    }
}

