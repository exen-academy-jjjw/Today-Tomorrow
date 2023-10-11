package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.domain.entity.OutMember;
import com.ezen.jjjw.domain.entity.RefreshToken;
import com.ezen.jjjw.dto.request.MemberDeleteReqDto;
import com.ezen.jjjw.dto.request.MemberLoginReqDto;
import com.ezen.jjjw.dto.request.MemberSignupReqDto;
import com.ezen.jjjw.dto.response.TokenDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.MemberRepository;
import com.ezen.jjjw.repository.OutMemberRepository;
import com.ezen.jjjw.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

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
 * 2023-10-08        sonjia       최초 생성
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private OutMemberRepository outMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    private MockHttpServletResponse response;

    @Test
    void success_createMember() {
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
                .nickname("testNick")
                .password(passwordEncoder.encode("password"))
                .build();

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

    @Test
    void success_login() {
        //given
        Member member = mock(Member.class).builder()
                .memberId("testUser")
                .nickname("testNickname")
                .password(passwordEncoder.encode("password"))
                .build();

        MemberLoginReqDto loginReqDto = new MemberLoginReqDto("testUser", "password");

        //stub
        when(memberRepository.findByMemberId(loginReqDto.getMemberId())).thenReturn(Optional.ofNullable(member));
        when(member.validatePassword(passwordEncoder, loginReqDto.getPassword())).thenReturn(true);

        //when
        ResponseEntity<?> responseEntity = memberService.login(loginReqDto, response);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(member.getNickname()));

        //verify
        verify(memberRepository, times(1)).findByMemberId(loginReqDto.getMemberId());
    }

    @Test
    void fail_login_cause_id() {
        //given
        MemberLoginReqDto loginReqDto = new MemberLoginReqDto("testUser", "password");

        //stub
        when(memberRepository.findByMemberId(loginReqDto.getMemberId())).thenReturn(Optional.empty());

        //when
        ResponseEntity<?> responseEntity = memberService.login(loginReqDto, response);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND));

        //verify
        verify(memberRepository, times(1)).findByMemberId(loginReqDto.getMemberId());
    }

    @Test
    void fail_login_cause_password() {
        //given
        Member member = mock(Member.class).builder()
                .memberId("testUser")
                .nickname("testNickname")
                .password(passwordEncoder.encode("password"))
                .build();

        MemberLoginReqDto loginReqDto = new MemberLoginReqDto("testUser", "password111");

        //stub
        when(memberRepository.findByMemberId(loginReqDto.getMemberId())).thenReturn(Optional.ofNullable(member));
        when(member.validatePassword(passwordEncoder, loginReqDto.getPassword())).thenReturn(false);

        //when
        ResponseEntity<?> responseEntity = memberService.login(loginReqDto, response);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST));

        //verify
        verify(memberRepository, times(1)).findByMemberId(loginReqDto.getMemberId());
    }

    @Test
    void success_logout() {
        //given
        Member member = Member.builder()
                .memberId("testUser")
                .nickname("testNickname")
                .password(passwordEncoder.encode("password"))
                .build();

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMemberId(member.getId());

        //stub
        when(tokenProvider.deleteRefreshToken(member)).thenCallRealMethod();

        //when
        ResponseEntity<Integer> responseEntity = memberService.logout(member);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(HttpServletResponse.SC_OK));

        //verify
        verify(tokenProvider, times(1)).deleteRefreshToken(member);
    }

    @Test
    void success_delete() {
        //given
        Member member = Member.builder()
                .memberId("testUser")
                .nickname("testNickname")
                .password(passwordEncoder.encode("password"))
                .build();

        MemberDeleteReqDto deleteReqDto = new MemberDeleteReqDto("password");

        //stub
        when(member.validatePassword(passwordEncoder, deleteReqDto.getPassword())).thenReturn(true);
        when(outMemberRepository.save(any(OutMember.class))).thenReturn(new OutMember());
        doNothing().when(memberRepository).delete(member);

        //when
        ResponseEntity<Integer> responseEntity = memberService.delete(deleteReqDto, member);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(HttpServletResponse.SC_OK));

        //verify
        verify(outMemberRepository, times(1)).save(any(OutMember.class));
        verify(memberRepository, times(1)).delete(member);
    }

    @Test
    void fail_delete() {
        //given
        Member member = Member.builder()
                .memberId("testUser")
                .nickname("testNickname")
                .password(passwordEncoder.encode("password"))
                .build();

        MemberDeleteReqDto deleteReqDto = new MemberDeleteReqDto("password");

        //stub
        when(member.validatePassword(passwordEncoder, deleteReqDto.getPassword())).thenReturn(false);

        //when
        ResponseEntity<Integer> responseEntity = memberService.delete(deleteReqDto, member);

        //then
        assertEquals(responseEntity, ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST));
    }
}