package com.ezen.jjjw.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * packageName    : com.ezen.jjjw.dto.response
 * fileName       : BkBoardResDto
 * author         : sonjia
 * date           : 2023-08-12
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-08-12        sonjia       최초 생성
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BkBoardResDto {
    private Long postId;
    private String nickname;
    private String category;
    private String title;
    private String content;
    private int completion;
    private int share;
    private int existReview;
    private int existComment;
}
