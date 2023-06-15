package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MypageRequestDto;
import com.ezen.jjjw.dto.response.MypageResponseDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    //맴버 정보 조회
    @Transactional
    public ResponseEntity<MypageResponseDto> getFindMember() {
        Member member = tokenProvider.getMemberFromAuthentication();
        MypageResponseDto memberInfo = MypageResponseDto.builder()
                .nickname(member.getNickname())
                .build();
        return ResponseEntity.ok(memberInfo);
    }

    //닉네임 변경
    @Transactional
    public ResponseEntity<Integer> updateNick(MypageRequestDto request) {
        Member member = tokenProvider.getMemberFromAuthentication();
//        Member memberName = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Member memberName = (memberRepository.findById(member.getId())).get();
        if(memberName == null) {
            log.info("존재하지 않는 사용자");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        memberName.updateNickname(request);
        memberRepository.save(memberName);

//        return ResponseEntity.ok("변경 완료되었습니다");
        log.info("닉네임 변경 완료");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> updatePassword(MypageRequestDto request) {
        Member member = tokenProvider.getMemberFromAuthentication();
//        Member memberPassword = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Member memberPassword = (memberRepository.findById(member.getId())).get();
        if(memberPassword == null) {
            log.info("존재하지 않는 사용자");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        if(!passwordEncoder.matches(request.getPassword(), memberPassword.getPassword())){
//            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
            log.info("비밀번호 불일치");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }
        memberPassword.updatePassword(passwordEncoder, request);
        memberRepository.save(memberPassword);

//        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
        log.info("비밀번호 변경 완료");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }
}
