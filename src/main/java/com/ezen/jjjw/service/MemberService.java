package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.domain.entity.OutMember;
import com.ezen.jjjw.dto.request.MemberDeleteReqDto;
import com.ezen.jjjw.dto.request.MemberLoginReqDto;
import com.ezen.jjjw.dto.request.MemberSignupReqDto;
import com.ezen.jjjw.dto.response.TokenDto;
import com.ezen.jjjw.exception.CustomExceptionHandler;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.MemberRepository;
import com.ezen.jjjw.repository.OutMemberRepository;
import com.ezen.jjjw.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * packageName    : com.member.member_jwt.service
 * fileName       : MemberService
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                실질적인 비즈니스 로직이 처리되는 클래스
 *                                추가적으로 @Transactional이 붙은 메서드는
 *                                메서드가 포함하고 있는 작업 중에 하나라도 실패할 경우 전체 작업을 취소한다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    // DB와 소통해야하므로 repository 선언
    private final MemberRepository memberRepository;
    // 비밀번호의 암호화를 위해 선언
    private final PasswordEncoder passwordEncoder;
    // jwt 사용을 위해 선언
    private final TokenProvider tokenProvider;
    //탈퇴한 회원 정보 저장
    private final OutMemberRepository outMemberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입 로직
    @Transactional
    public ResponseEntity<?> createMember(MemberSignupReqDto memberSignupReqDto) {
        // 사용자로부터 입력받은 memberId로 DB에 같은 아이디가 있는지 확인
        if (null != isPresentMember(memberSignupReqDto.getMemberId())) {
            log.info("중복된 아이디");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
//            return ResponseEntity.badRequest().build();
        }

        // 사용자로부터 입력받은 nickname로 DB에 같은 닉네임이 있는지 확인
        Optional<Member> byNickname = memberRepository.findByNickname(memberSignupReqDto.getNickname());
        if(byNickname.isPresent()) {
            log.info("닉네임 중복");
            return ResponseEntity.ok("nickname " + HttpServletResponse.SC_BAD_REQUEST);
        }

        // 비밀번호와 비밀번호 확인의 값이 서로 일치하는지 확인
        if (!memberSignupReqDto.getPassword().equals(memberSignupReqDto.getPasswordConfirm())) {
            log.info("비밀번호 불일치");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        Member member = Member.builder()
                .memberId(memberSignupReqDto.getMemberId())
                .nickname(memberSignupReqDto.getNickname())
                .password(passwordEncoder.encode(memberSignupReqDto.getPassword()))
                .build();
        memberRepository.save(member);
        log.info("회원가입 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    // 로그인 로직
    @Transactional
    public ResponseEntity<?> login(MemberLoginReqDto memberLoginReqDto, HttpServletResponse response) {
        // 사용자로부터 입력받은 아이디를 통해 DB에 해당 아이디가 존재하는지 확인
        Member member = isPresentMember(memberLoginReqDto.getMemberId());
        if (null == member) {
            log.info("존재하지 않는 회원");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        // 사용자로부터 입력받은 비밀번호와 DB상의 비밀번호가 일치하는지 확인
        if (!member.validatePassword(passwordEncoder, memberLoginReqDto.getPassword())) {
            log.info("비밀번호 불일치");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        // 위 과정을 모두 거치고 나면 정상적인 로그인으로 보고 토큰을 생성하여 헤더에 담아 보냄
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenProvider.tokenToHeaders(tokenDto, response);
        log.info("로그인 성공");
        return ResponseEntity.ok(member.getNickname());
    }

    // 로그아웃 로직
    @Transactional
    public ResponseEntity<Integer> logout(Member member) {
        tokenProvider.deleteRefreshToken(member);
        log.info("로그아웃 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    // 회원 탈퇴 로직
    @Transactional
    public ResponseEntity<Integer> delete(MemberDeleteReqDto memberDeleteReqDto, Member member) {
        if (!member.validatePassword(passwordEncoder, memberDeleteReqDto.getPassword())) {
            log.info("비밀번호 불일치");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        OutMember outMember = OutMember.builder()
                .mId(member.getId())
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .password(passwordEncoder.encode(member.getPassword())).build();
        outMemberRepository.save(outMember);
        refreshTokenRepository.deleteByMemberId(member.getId());
        memberRepository.delete(member);

        log.info("회원탈퇴 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    // 파라미터로 넘어온 memberId를 이용해 DB에서 memberId를 갖고 있는 데이터가 있는지 확인함
    @Transactional(readOnly = true)
    public Member isPresentMember(String memberId) {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        return optionalMember.orElse(null);
    }
}
