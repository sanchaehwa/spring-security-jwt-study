package com.example.springjwt.service;

import com.example.springjwt.dto.CustomUserDetails;
import com.example.springjwt.entity.UserEntity;
import com.example.springjwt.global.error.ErrorCode;
import com.example.springjwt.global.error.exception.NotFoundUserException;
import com.example.springjwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //DB에서 조회
        UserEntity userData = userRepository.findByUsername(username);

        if (userData != null) {
            // 조회한 사용자 정보를 기반으로 CustomUserDetails 객체 생성 후 반환
            // AuthenticationManager가 이 UserDetails를 사용하여 요청된 사용자 정보와 비교해 인증 수행
            return new CustomUserDetails(userData);
        }
        // 사용자가 존재하지 않으면 예외 발생
        throw new NotFoundUserException(ErrorCode.NOT_FOUND_RESOURCE);
    }


}
