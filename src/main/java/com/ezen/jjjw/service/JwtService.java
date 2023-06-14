package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.domain.entity.RefreshToken;
import com.ezen.jjjw.dto.response.TokenDto;
import com.ezen.jjjw.exception.CustomException;
import com.ezen.jjjw.exception.ErrorCode;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * packageName    : com.member.member_jwt.service
 * fileName       : JwtService
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                토큰 재발급을 받기 위한 service
 */
@RequiredArgsConstructor
@Service
public class JwtService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    // 토큰 재발급 로직
    @Transactional
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 엑세스 토큰안에 담긴 정보를 이용해 DB상에 실제 사용자가 존재하는지 확인
        String accessToken = tokenProvider.resolveToken(request);
        Member member = memberRepository.findByMemberId(tokenProvider.getClaimsMemberId(accessToken)).get();
        if (null == member) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }

        // 리프레시 토큰 유효성 검증
        RefreshToken refreshToken = tokenProvider.isPresentRefreshToken(member);
        if (!refreshToken.getValue().equals(request.getHeader("RefreshToken"))) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 위 과정을 다 통과하면 새로운 토큰을 만들어 response에 담아 보냄
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        // 리프레시 토큰도 새로 발급해 DB에 저장
        refreshToken.updateValue(tokenDto.getRefreshToken());
        tokenProvider.tokenToHeaders(tokenDto, response);
        return ResponseEntity.ok("success");
    }
}
