package com.ezen.jjjw.impl;

import com.ezen.jjjw.domain.Authority;
import com.ezen.jjjw.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * packageName    : com.member.member_jwt.entity
 * fileName       : UserDetailsImpl
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                내장되어 있는 UserDetails를 사용하기 위해 생성
 *                                스프링 시큐리티는 UserDetails 인터페이스를 구현한 클래스(UserDetailsImpl)를
 *                                사용자로 보고 작업을 함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    // 계정 객체를 선언하여 아래 오버라이드에 사용
    private Member member;

    // 계정이 가지고 있는 권한 목록을 리턴
    // 아래는 enum 클래스로 저장한 계정 중 일반 계정으로 정의된 것을 불러와 선언한다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(Authority.ROLE_MEMBER.toString());
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        return authorities;
    }

    // 계정이 가지고 있는 비밀번호를 리턴
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 계정이 가지고 있는 이름을 리턴
     * 이때 주의할 점은 getUsername은 스프링 시큐리티가 가지고 있는 메서드명이고
     * 리턴값으로는 우리가 실제로 사용하게 될 계정 객체 안의 고유한 이름을 반환시켜야 함
     */
    @Override
    public String getUsername() {
        return member.getMemberId();
    }

    // 계정이 만료되었는지의 여부를 리턴(true가 만료되지 않음을 의미)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있는지의 여부를 리턴(true가 잠겨있지 않음을 의미)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정의 비밀번호가 만료되지 않았는지의 여부를 리턴(true가 만료되지 않음을 의미)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 사용 가능한 계정인지를 리턴(true가 사용가능함을 의미)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
