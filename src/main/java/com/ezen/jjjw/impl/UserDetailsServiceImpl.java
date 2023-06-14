package com.ezen.jjjw.impl;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * packageName    : com.member.member_jwt.impl
 * fileName       : UserDetailsServiceImpl
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                https://velog.io/@jyyoun1022/SpringSpring-Security%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8%EC%B2%98%EB%A6%AC-5
 *                                위 링크를 통해 UserDedailsService에 대해 간략하게 훑어보면 아래 내용을 보는 데에 도움이 될 것 같다
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    // 식별값으로 회원 정보를 불러와 사용자의 존재 유무를 판별. jwt에 사용할 예정
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        return member
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
