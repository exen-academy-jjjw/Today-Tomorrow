package com.ezen.jjjw.controller;

import com.ezen.jjjw.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * packageName    : com.member.member_jwt.controller
 * fileName       : JwtController
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                토큰 재발급을 받기 위한 controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/member")
public class JwtController {

    private final JwtService jwtService;

    @PostMapping(value = "/reissue")
    public ResponseEntity<Integer> reissue(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.reissue(request, response);
    }
}
