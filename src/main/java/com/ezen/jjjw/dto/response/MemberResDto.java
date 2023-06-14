package com.ezen.jjjw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName    : com.member.member_jwt.dto.res
 * fileName       : MemberResDto
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                entity타입의 정보에서 필요한 것만 담아 보내기 위한 dto
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResDto {
    private Long id;
    private String memberId;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
