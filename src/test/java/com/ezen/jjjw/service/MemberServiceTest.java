package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MemberSignupReqDto;
import com.ezen.jjjw.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.ezen.jjjw.service
 * fileName       : MemberServiceTest
 * author         : wldk9
 * date           : 2023-10-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-10-08        wldk9       최초 생성
 */
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createMember() {
        MemberSignupReqDto requestDto = new MemberSignupReqDto(
                "testUser",
                "testNickname",
                "password",
                "password");

        // memberRepository.findByNickname() 메서드의 행동 정의
        when(memberRepository.findByNickname(requestDto.getNickname())).thenReturn(Optional.empty());

        // passwordEncoder.encode() 메서드의 행동 정의
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");

        // memberRepository.save() 메서드의 행동 정의
        when(memberRepository.save(any(Member.class))).thenReturn(new Member());

        // 테스트 대상 메서드 호출
        ResponseEntity<?> responseEntity = memberService.createMember(requestDto);

        // 테스트 결과 검증
        assertEquals(ResponseEntity.ok(200), responseEntity);

        // memberRepository.findByNickname() 메서드가 호출되었는지 검증
        verify(memberRepository, times(1)).findByNickname(requestDto.getNickname());

        // passwordEncoder.encode() 메서드가 호출되었는지 검증
        verify(passwordEncoder, times(1)).encode(requestDto.getPassword());

        // memberRepository.save() 메서드가 호출되었는지 검증
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}