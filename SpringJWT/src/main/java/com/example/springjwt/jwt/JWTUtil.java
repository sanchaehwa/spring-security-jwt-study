package com.example.springjwt.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
//JWT 0.12.3 버전
public class JWTUtil {

    private SecretKey secretKey;

    //비밀키 사용해서 비밀키 객체를 생성
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) { //application.* 에서 spring.jwt.secret 키 주입
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
    //토큰에서 Username을 추출
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().
                parseSignedClaims(token) //서명이 포함된 JWT 검증 , 유효하면 클레임 객체를 가지고 옴- 클레임객체는 username
                .getPayload()
                .get("username", String.class);
    }
    //토큰에서 (권한) Role 추출
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).
                getPayload()
                .get("role", String.class);
    }
    //토큰 만료 여부
    public Boolean isExpired(String token) {
        return Jwts.parser().
                verifyWith(secretKey).
                build()
                .parseSignedClaims(token).
                 getPayload()
                .getExpiration() //토큰 만료 날짜를 가지고옴
                .before(new Date());
    }
    //Access Token인지, Refresh Token 인지
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    //사용자 이름, 역할 및 만료 시간을 기준으로 JWT 생성
    public String createJwt(String category,String username, String role, Long expiredMs) {
        return Jwts.builder()
                //claim() 데이터 추가 JWT 페이로드
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                //토큰 만료시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();

    }

}
