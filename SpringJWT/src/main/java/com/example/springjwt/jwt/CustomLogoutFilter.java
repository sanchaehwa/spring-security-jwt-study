package com.example.springjwt.jwt;

import com.example.springjwt.config.RedisUtil;
import com.example.springjwt.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private ResponseEntity<String> doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/logout")) {
            filterChain.doFilter(request, response);
            return null;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return null;
        }

        //프론트엔드측 : 로컬 스토리지에 존재하는 Access 토큰 삭제 및 서버측 로그아웃 경로로 Refresh 토큰 전송
        //백엔드: 서버측 로그아웃 경로로 넘어온 Refresh 토큰 받아야함.
        String refresh = null;
        //쿠키에서 Refresh 토큰 찾기

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        if (refresh == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is empty");
        }
        //만료된 Refresh Token
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }
        //Refresh Token인지 확인
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            //refresh 토큰이 아닌경우
            return new ResponseEntity<>("refresh token is invalid", HttpStatus.BAD_REQUEST);
        }
        String username = jwtUtil.getUsername(refresh);

        //Redis에 저장되어있는지 확인
        if (redisUtil.getRefreshToken(username) == null) {
            return new ResponseEntity<>("refresh token is empty", HttpStatus.BAD_REQUEST);
        }
        //로그아웃 진행
        if (redisUtil.getRefreshToken(username) != null) {
            redisUtil.deleteRefreshToken(username);
        }
        //쿠키 초기화
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        return new ResponseEntity<>("success", HttpStatus.OK);

    }


}
