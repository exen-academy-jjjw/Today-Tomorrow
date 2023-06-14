package com.ezen.jjjw.controller;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MemberDeleteReqDto;
import com.ezen.jjjw.dto.request.MemberLoginReqDto;
import com.ezen.jjjw.dto.request.MemberSignupReqDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * packageName    : com.member.member_jwt.controller
 * fileName       : MemberController
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                뷰에서의 요청을 받아 서비스단과 연결짓는 역할을 하는 클래스
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/member")
public class MemberController {

    // 서비스단의 메서드를 사용하기 위해 필드로 선언
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    // 회원가입
//    @PostMapping(value = "/signup")
//    public ResponseEntity<?> signup(@RequestBody @Valid MemberSignupReqDto memberSignupReqDto) {
//        return memberService.createMember(memberSignupReqDto);
//    }
    @PostMapping(value = "/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid MemberSignupReqDto memberSignupReqDto) {
        return memberService.createMember(memberSignupReqDto);
    }

    // 로그인, 토큰을 담아 보내야 하기 때문에 HttpServletResponse도 함께 넣는다.
//    @PostMapping(value = "/login")
//    public ResponseEntity<?> login(@RequestBody @Valid MemberLoginReqDto memberLoginReqDto, HttpServletResponse response) {
//        return memberService.login(memberLoginReqDto, response);
//    }
    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody @Valid MemberLoginReqDto memberLoginReqDto, HttpServletResponse response) {
        return memberService.login(memberLoginReqDto, response);
    }

    // 로그아웃, 사용자로부터 받아야할 정보가 따로 없는 대신 토큰을 봐야하기때문에 HttpServletRequest만 파라미터로 입력한다.
//    @GetMapping(value = "/logout")
//    public ResponseEntity<?> logout() {
//        Member member = tokenProvider.getMemberFromAuthentication();
//        return memberService.logout(member);
//    }
    @GetMapping(value = "/logout")
    public ResponseEntity<String> logout() {
        Member member = tokenProvider.getMemberFromAuthentication();
        return memberService.logout(member);
    }

    // 회원 탈퇴
//    @PostMapping(value = "/delete")
//    public ResponseEntity<?> delete(@RequestBody @Valid MemberDeleteReqDto memberDeleteReqDto) {
//        Member member = tokenProvider.getMemberFromAuthentication();
//        return memberService.delete(memberDeleteReqDto, member);
//    }
    @PostMapping(value = "/delete")
    public ResponseEntity<String> delete(@RequestBody @Valid MemberDeleteReqDto memberDeleteReqDto) {
        Member member = tokenProvider.getMemberFromAuthentication();
        return memberService.delete(memberDeleteReqDto, member);
    }

}
