package com.ezen.jjjw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.member.member_jwt.dto.response
 * fileName       : TokenDto
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                로그인시 담아 보낼 토큰의 정보를 담은 dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    // 권한부여 유형
    private String grantType;
    private String accessToken;
    private String refreshToken;
    // 엑세스 토큰의 만료 시간
    private Long accessTokenExpiresIn;
}
