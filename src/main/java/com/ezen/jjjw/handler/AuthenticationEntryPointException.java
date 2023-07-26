package com.ezen.jjjw.handler;

import com.ezen.jjjw.dto.response.ResponseDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * packageName    : com.member.member_jwt.handler
 * fileName       : AuthenticationEntryPointException
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                SecurityConfiguration에서 사용하기 위한 핸들러
 */
@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointException implements AuthenticationEntryPoint {
    private final TokenProvider tokenProvider;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (!tokenProvider.validateToken(tokenProvider.resolveToken(request))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(
                new ObjectMapper().writeValueAsString(
                        ResponseDto.fail("BAD_REQUEST", "로그인이 필요합니다.")
                )
        );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
