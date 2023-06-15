package com.ezen.jjjw.controller;

import com.ezen.jjjw.dto.request.MypageRequestDto;
import com.ezen.jjjw.dto.response.MypageResponseDto;
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

    //전체 조회
    @GetMapping("/mypage")
    public ResponseEntity<MypageResponseDto> getMypageMember() {
        return mypageService.getFindMember();
    }

    //닉네임변경
    @PutMapping("/update/nickname")
    public ResponseEntity<Integer> updateNickname(@RequestBody @Valid MypageRequestDto request) {
        return mypageService.updateNick(request);
    }

    //비밀번호 변경
    @PutMapping("/update/password")
    public ResponseEntity<Integer> updatePassword(@RequestBody @Valid MypageRequestDto request) {
        return mypageService.updatePassword(request);
    }
}
