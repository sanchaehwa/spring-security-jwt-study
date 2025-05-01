package com.example.springjwt.jwt;

import com.example.springjwt.dto.CustomUserDetails;
import com.example.springjwt.global.error.ErrorCode;
import com.example.springjwt.global.error.exception.NotFoundUserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JWTUtil jwtUtil;



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //클라이언트 요청에서 사용자 Username , Password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);


        //Authentication Manager 가 검증을 할 수 있도록 값을 토큰에 담아 넘겨줌
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,null);

        return authenticationManager.authenticate(authToken);
    }
    //로그인 성공  - JWT 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        System.out.println(" successful authentication");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        // 로그인에 성공한 사용자 정보를 활용해서
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); // 권한 목록 가져오기
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator(); // 반복자 생성
        GrantedAuthority auth = iterator.next(); // 첫 번째 권한 가져오기
        String role = auth.getAuthority();//권한 추출

        //JWT 생성
        long tenHoursMs = 10 * 60 * 60 * 1000L;
        String token = jwtUtil.createJwt(username, role, tenHoursMs);  //토큰 발급 *60*60&10L : 토큰 유효시간 18시간

        response.addHeader("Authorization", "Bearer " + token);



    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }
}
