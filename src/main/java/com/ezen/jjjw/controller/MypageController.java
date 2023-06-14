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
//    @GetMapping("/mypage")
//    public ResponseEntity<?> getMypageMember() {
//        return ResponseEntity.ok().body(mypageService.getFindMember());
//    }
    @GetMapping("/mypage")
    public ResponseEntity<MypageResponseDto> getMypageMember() {
        return mypageService.getFindMember();
    }

    //닉네임변경
//    @PutMapping("/update/nickname")
//    public ResponseEntity<?> updateNickname(@RequestBody @Valid MypageRequestDto request) {
//        mypageService.updateNick(request);
//        return ResponseEntity.ok().body("변경 완료되었습니다");
//    }
    @PutMapping("/update/nickname")
    public ResponseEntity<String> updateNickname(@RequestBody @Valid MypageRequestDto request) {
        return mypageService.updateNick(request);
    }

    //비밀번호 변경
//    @PutMapping("/update/password")
//    public ResponseEntity<?>updatePassword(@RequestBody @Valid MypageRequestDto request) {
//        return ResponseEntity.ok().body(mypageService.updatePassword(request));
//    }
    @PutMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid MypageRequestDto request) {
        return mypageService.updatePassword(request);
    }
}
