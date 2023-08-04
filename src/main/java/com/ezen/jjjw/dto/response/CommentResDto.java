package com.ezen.jjjw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * packageName    : com.ezen.jjjw.dto.response
 * fileName       : CommentResDto.java
 * author         : won
 * date           : 2023-07-28
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-07-28        won       최초 생성
 */

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResDto {
    private Long id;
    private String commentTxt;
    private String nickname;
    private Long postId;
    private Long parent;
    private List<CommentResDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public void setChildren(List<CommentResDto> children) {
        this.children = children;
    }
}
