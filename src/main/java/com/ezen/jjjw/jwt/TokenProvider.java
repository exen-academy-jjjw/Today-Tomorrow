package com.ezen.jjjw.jwt;


import com.ezen.jjjw.domain.Authority;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.domain.entity.RefreshToken;
import com.ezen.jjjw.dto.response.ResponseDto;
import com.ezen.jjjw.dto.response.TokenDto;
import com.ezen.jjjw.impl.UserDetailsImpl;
import com.ezen.jjjw.impl.UserDetailsServiceImpl;
import com.ezen.jjjw.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

/**
 * packageName    : com.member.member_jwt.jwt
 * fileName       : TokenProvider
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 */
@Slf4j // 로깅 편하게 하려고 쓰는 어노테이션
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            //30분
    private static final long REFRESH_TOKEN_EXPRIRE_TIME = 1000 * 60 * 60 * 24 * 7;     //7일
    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsServiceImpl userDetailsService;

    // 복잡해보이지만 기본생성자니까 겁먹지 말자
    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenRepository refreshTokenRepository, UserDetailsServiceImpl userDetailsService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성 로직
    public TokenDto generateTokenDto(Member member) {
        long now = (new Date().getTime());

        // 현재 시간 + 토큰 유효 시간
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        /* *
         * 엑세스 토큰
         * 회원의 memberId, 권한, 만료시간과 암호화 방식을 담는다.
         */
        String accessToken = Jwts.builder()
                .setSubject(member.getMemberId())
                .claim(AUTHORITIES_KEY, Authority.ROLE_MEMBER.toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        /* *
         * 리프레시 토큰
         * 만료시간과 암호화 방식을 담아 토큰을 생성하고
         * RefreshToken Entity 객체를 생성해 그 안에 정보는 담아 DB에 저장한다.
         */
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPRIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(member.getId())
                .member(member)
                .value(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenObject);

        // TokenDto객체를 생성해 위의 정보들을 하나하나 담아 리턴한다.
        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    // 추후 서비스단에서 들어온 토큰 안에 담긴 member정보를 추출하기 위한 코드
    public Member getMemberFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return null;
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getMember();
    }

    // 토큰의 유효성 검증을 위한 코드
    public boolean validateToken(String token) {
        try {
            // 입력받은 토큰값을 Jwts라는 객체로 만들어 아래 검사를 진행한다
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰입니다.");
        }
        return false;
    }

    // member 객체를 파라미터로 받아 DB에 토큰이 있는지 확인하기 위한 코드
    @Transactional(readOnly = true)
    public RefreshToken isPresentRefreshToken(Member member) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMemberId(member.getId());
        return optionalRefreshToken.orElse(null);
    }

    /* *
    * 로그아웃을 했거나 회원 탈퇴를 했을 때, DB상에서 회원 정보뿐 아니라 토큰도 지워져야 함.
    * 이때 DB에서 저장해두었던 토큰을 삭제하기 위한 코드
    */
    @Transactional
    public ResponseDto<?> deleteRefreshToken(Member member) {
        RefreshToken refreshToken = isPresentRefreshToken(member);
        if (null == refreshToken) {
            return ResponseDto.fail("TOKEN_NOT_FOUND", "존재하지 않는 Token 입니다.");
        }

        refreshTokenRepository.delete(refreshToken);
        return ResponseDto.success("success");
    }

    // 엑세스 토큰의 복호화를 위한 코드 아래 getAuthentication메서드에서 사용됨
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰을 사용해 해당 사용자가 본인이 맞는지 인증 정보를 조회한다. 아직 미사용
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰 입니다.");
        }

        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    // 엑세스 토큰 안에서 memberId를 꺼내오기 위한 메서드
    public String getClaimsMemberId(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰 입니다.");
        }

        return claims.getSubject();
    }

    // 토큰을 헤더에 보낼때 어떤 정보들을 담아 보낼지에 대한 메서드
    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("RefreshToken", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

    // HTTP Request의 Header에서 Bearer글자를 떼고 토큰값만 받아오기 위한 메서드
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = (request.getHeader("Authorization"));
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // delete 확인
    public String getMemberIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 현재 로그인한 멤버의 ID를 추출하여 반환
    public String getLoggedInMemberId(HttpServletRequest request) {
        String token = resolveToken(request);
        if (StringUtils.hasText(token)) {
            return getClaimsMemberId(token);
        }
        return null;
    }
}
