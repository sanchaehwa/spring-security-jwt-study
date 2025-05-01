package com.example.springjwt.dto;

import com.example.springjwt.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@RequiredArgsConstructor

public class CustomUserDetails implements UserDetails {
    private final UserEntity userEntity;

    @Override //권한
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>(); //권한정보를 담기위한

        collection.add(new GrantedAuthority(){
            @Override
            public String getAuthority() {
                return userEntity.getRole(); //사용자 엔티티로부터 권한 반환
            }
        });
        return collection;

    }

    @Override
    public String getPassword() {
        return  userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return  userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { //계정 만료
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { //계정 잠금
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {//비밀번호 만료
        return true;
    }

    @Override
    public boolean isEnabled() { //계정이 활성화
        return true;
    }

}
