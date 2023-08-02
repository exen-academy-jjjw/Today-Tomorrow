package com.ezen.jjjw.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.ezen.jjjw.dto.request
 * fileName       : CommentReqDto.java
 * author         : won
 * date           : 2023-07-28
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-07-28        won       최초 생성
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentReqDto {
    private String CommentTxt;
    private Long memberId;
    private Long postId;
    private Long parentId;
}
