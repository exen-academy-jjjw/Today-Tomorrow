package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MypageRequestDto;
import com.ezen.jjjw.dto.response.MypageResponseDto;
import com.ezen.jjjw.exception.CustomException;
import com.ezen.jjjw.exception.ErrorCode;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResponseEntity<String> updateNick(MypageRequestDto request) {
        Member member = tokenProvider.getMemberFromAuthentication();
        Member memberName = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        memberName.updateNickname(request);
        memberRepository.save(memberName);

        return ResponseEntity.ok("변경 완료되었습니다");
    }

    @Transactional
    public ResponseEntity<String> updatePassword(MypageRequestDto request) {
        Member member = tokenProvider.getMemberFromAuthentication();
        Member memberPassword = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        if(!passwordEncoder.matches(request.getPassword(), memberPassword.getPassword())){
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }
        memberPassword.updatePassword(passwordEncoder, request);
        memberRepository.save(memberPassword);

        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
    }
}
