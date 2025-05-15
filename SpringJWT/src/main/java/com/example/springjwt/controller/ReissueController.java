package com.example.springjwt.controller;

//Refresh - Access 재발급 받는 단계 Reissue

import com.example.springjwt.config.RedisUtil;
import com.example.springjwt.entity.UserEntity;
import com.example.springjwt.jwt.JWTUtil;
import com.example.springjwt.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){

        //refresh Token
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

        if (refresh == null){
            //response status code
            //return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is empty");
        }
        //만료시, 400에러
        try {
            jwtUtil.isExpired(refresh);
        }catch (ExpiredJwtException e){
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        //토큰이 refresh 인지 확인
        String category = jwtUtil.getCategory(refresh);

        if(!category.equals("refresh")){
            //refresh 토큰이 아닌경우
            return new ResponseEntity<>("refresh token is invalid", HttpStatus.BAD_REQUEST);
        }
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        UserEntity userData = userRepository.findByUsername(username);

        //Redis에서 저장된  Refresh 토큰과 비교(Cookie 는 사용자가 보낸거, Redis는 서버가 가지고 있는것)
        String redisRefresh = redisUtil.getRefreshToken(username);

        if(!refresh.equals(redisRefresh)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is invalid");
        }

        redisUtil.deleteRefreshToken(username); //새로운 Refresh 토큰으로 갱신하기 위해

        //Access 생성
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        //Refresh 생성 (Refresh Rotate)-Access +  Refresh 재발급
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 1800000L); //30qns

        redisUtil.saveRefreshToken(username, newRefresh);
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>("success", HttpStatus.OK);

    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }


}
