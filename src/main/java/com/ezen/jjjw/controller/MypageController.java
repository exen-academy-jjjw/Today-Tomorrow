package com.ezen.jjjw.controller;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MypageRequestDto;
import com.ezen.jjjw.dto.response.MypageResponseDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/member")
public class MypageController {

    private final MypageService mypageService;
    private final TokenProvider tokenProvider;

    //전체 조회
    @GetMapping("/mypage")
    public ResponseEntity<MypageResponseDto> getMypageMember() {
        Member member = tokenProvider.getMemberFromAuthentication();
        return mypageService.getFindMember(member);
    }

    //닉네임변경
    @PutMapping("/update/nickname")
    public ResponseEntity<Integer> updateNickname(@RequestBody @Valid MypageRequestDto request) {
        Member member = tokenProvider.getMemberFromAuthentication();
        return mypageService.updateNick(request, member);
    }

    //비밀번호 변경
    @PutMapping("/update/password")
    public ResponseEntity<Integer> updatePassword(@RequestBody @Valid MypageRequestDto request) {
        Member member = tokenProvider.getMemberFromAuthentication();
        return mypageService.updatePassword(request, member);
    }
}
