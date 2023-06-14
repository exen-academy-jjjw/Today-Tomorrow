package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MypageRequestDto;
import com.ezen.jjjw.dto.response.MypageResponseDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    //맴버 정보 조회
    public MypageResponseDto getFindMember() {
        Member member = tokenProvider.getMemberFromAuthentication();

        MypageResponseDto memberInfo = MypageResponseDto.builder()
                .nickname(member.getNickname())
                .build();

        return memberInfo;
    }

    //닉네임 변경
    public void updateNick(MypageRequestDto request) {
        Member member = tokenProvider.getMemberFromAuthentication();
        Member memberName = memberRepository.findById(member.getId()).orElseThrow(() -> new RuntimeException("유저가 없습니다"));

        memberName.updateNickname(request);
        memberRepository.save(memberName);
    }

    public String updatePassword(MypageRequestDto request) {
        Member member = tokenProvider.getMemberFromAuthentication();
        Member memberPassword = memberRepository.findById(member.getId()).orElseThrow(() -> new RuntimeException("유저가 없습니다"));
        if(!passwordEncoder.matches(request.getPassword(), memberPassword.getPassword())){
            return "INVALID_TOKEN";
        }
        memberPassword.updatePassword(passwordEncoder, request);
        memberRepository.save(memberPassword);
        return "비밀번호 변경이 완료되었습니다";
    }
}
