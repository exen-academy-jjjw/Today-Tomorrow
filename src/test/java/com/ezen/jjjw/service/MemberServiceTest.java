package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MemberSignupReqDto;
import com.ezen.jjjw.dto.response.MemberResDto;
import com.ezen.jjjw.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void seccess_createMember() {
        //given
        MemberSignupReqDto memberSignupReqDto = new MemberSignupReqDto();
        memberSignupReqDto.setMemberId("testUser");
        memberSignupReqDto.setNickname("testNickname");
        memberSignupReqDto.setPassword("password");
        memberSignupReqDto.setPasswordConfirm("password");

        //stub
        when(memberRepository.findByMemberId(memberSignupReqDto.getMemberId())).thenReturn(Optional.empty());
        when(memberRepository.findByNickname(memberSignupReqDto.getNickname())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(new Member());

        //when
        ResponseEntity<?> responseEntity = memberService.createMember(memberSignupReqDto);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(HttpServletResponse.SC_OK));

        //verify
        verify(memberRepository, times(1)).findByMemberId(memberSignupReqDto.getMemberId());
        verify(memberRepository, times(1)).findByNickname(memberSignupReqDto.getNickname());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void fail_createMember_cause_id() {
        //given
        Member member = mock(Member.class).builder()
                .memberId("testUser")
                .nickname("testNcik")
                .password(passwordEncoder.encode("password")).build();

        MemberSignupReqDto memberSignupReqDto = new MemberSignupReqDto();
        memberSignupReqDto.setMemberId("testUser");
        memberSignupReqDto.setNickname("testNickname");
        memberSignupReqDto.setPassword("password");
        memberSignupReqDto.setPasswordConfirm("password");

        //stub
        when(memberRepository.findByMemberId(memberSignupReqDto.getMemberId())).thenReturn(Optional.ofNullable(member));

        //when
        ResponseEntity<?> responseEntity = memberService.createMember(memberSignupReqDto);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST));

        //verify
        verify(memberRepository, times(1)).findByMemberId(memberSignupReqDto.getMemberId());
    }

    @Test
    void fail_createMember_cause_nickname() {
        //given
        Member member = mock(Member.class).builder()
                .memberId("testUser1")
                .nickname("testNickname")
                .password(passwordEncoder.encode("password")).build();

        MemberSignupReqDto memberSignupReqDto = new MemberSignupReqDto();
        memberSignupReqDto.setMemberId("testUser2");
        memberSignupReqDto.setNickname("testNickname");
        memberSignupReqDto.setPassword("password");
        memberSignupReqDto.setPasswordConfirm("password");

        //stub
        when(memberRepository.findByMemberId(memberSignupReqDto.getMemberId())).thenReturn(Optional.empty());
        when(memberRepository.findByNickname(memberSignupReqDto.getNickname())).thenReturn(Optional.ofNullable(member));

        //when
        ResponseEntity<?> responseEntity = memberService.createMember(memberSignupReqDto);

        //then
        assertEquals(responseEntity, ResponseEntity.ok("nickname " + HttpServletResponse.SC_BAD_REQUEST));

        //verify
        verify(memberRepository, times(1)).findByMemberId(memberSignupReqDto.getMemberId());
        verify(memberRepository, times(1)).findByNickname(memberSignupReqDto.getNickname());
    }

    @Test
    void fail_createMember_cause_password() {
        //given
        MemberSignupReqDto memberSignupReqDto = new MemberSignupReqDto();
        memberSignupReqDto.setMemberId("testUser");
        memberSignupReqDto.setNickname("testNickname");
        memberSignupReqDto.setPassword("password");
        memberSignupReqDto.setPasswordConfirm("password1111");

        //when
        ResponseEntity<?> responseEntity = memberService.createMember(memberSignupReqDto);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST));
        assertFalse(Boolean.parseBoolean(memberSignupReqDto.getPassword()), memberSignupReqDto.getPasswordConfirm());
    }
}