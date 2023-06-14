package com.ezen.jjjw.dto;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.response.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
public class BkBoardDto {

    private Long memberid; // 유저 식별자
    private Long postId; // 게시판 식별자
    private String category; // 카테고리
    private String title;  // 제목
    private String content;  // 내용
    private LocalDateTime createAt; // 생성일자
    private LocalDateTime modifiedAt; // 수정일자

//    public static class Response{
//        private Long post_id;
//        private String content;
//        private String title;
//
//    }
    @Getter
    @NoArgsConstructor
    public static class Request {
        private String memberId;
        private Long postId; // 게시판 식별자
        private String category; // 카테고리
        private String title;  // 제목
        private String content;  // 내용

        @Builder
        public void Request(Long post_id, String category, String title, String content) {
            this.postId = post_id;
            this.category = category;
            this.title = title;
            this.content = content;
        }

        public BkBoard toEntity(Member member) {
            return BkBoard.builder()
                    .member(member)
                    .postid(postId)
                    .category(category)
                    .title(title)
                    .content(content)
                    .build();
        }

    public void setMemberId(String memberId) {
    }
}

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long postId; // 게시판 식별자
        private String category; // 카테고리
        private String title;  // 제목
        private String content;  // 내용
        private LocalDateTime createAt; // 생성일자
        private LocalDateTime modifiedAt; // 수정일자

        @Builder
        public Response(Long postid, String category, String title, String content, LocalDateTime createdAt, LocalDateTime modifiedAt, String memberId) {


            this.postId = postid;
            this.category = category;
            this.title = title;
            this.content = content;
            this.createAt = createdAt;
            this.modifiedAt = modifiedAt;
        }

        public static ResponseDto<Object> toResponseDto(Response response) {
            return ResponseDto.success(response);
        }

        public static Response of(BkBoard bkBoard) {
            return builder()
                    .postid(bkBoard.getPostid())
                    .title(bkBoard.getTitle())
                    .content(bkBoard.getContent())
                    .createdAt(bkBoard.getCreatedAt())
                    .modifiedAt(bkBoard.getModifiedAt())
                    .build();
        }
    }
}
