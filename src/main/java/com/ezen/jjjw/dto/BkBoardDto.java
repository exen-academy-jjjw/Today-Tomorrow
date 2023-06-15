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

    private Long memberId; // 유저 식별자
    private Long postId; // 게시판 식별자
    private String category; // 카테고리
    private String title;  // 제목
    private String content;  // 내용
    private Integer completion; // 완료 여부
    private LocalDateTime createAt; // 생성일자
    private LocalDateTime modifiedAt; // 수정일자

    @Getter
    @NoArgsConstructor
    public static class Request {
//        private String memberId;
        private Long postId; // 게시판 식별자
        private String category; // 카테고리
        private String title;  // 제목
        private String content;  // 내용

        public BkBoard toEntity(Member member) {
            return BkBoard.builder()
                    .member(member)
                    .postId(postId)
                    .category(category)
                    .title(title)
                    .content(content)
                    .build();
        }
}

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        //        private String memberId;
        private Long postId; // 게시판 식별자
        private String category; // 카테고리
        private String title;  // 제목
        private String content;  // 내용
        private Integer completion;

        public BkBoard toEntity(Member member) {
            return BkBoard.builder()
                    .member(member)
                    .postId(postId)
                    .category(category)
                    .title(title)
                    .content(content)
                    .completion(completion)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long postId; // 게시판 식별자
        private String category; // 카테고리
        private String title;  // 제목
        private String content;  // 내용
        private Integer completion; // 완료 여부
        private LocalDateTime createAt; // 생성일자
        private LocalDateTime modifiedAt; // 수정일자

        @Builder
        public Response(Long postId, String category, String title, String content, LocalDateTime createdAt, LocalDateTime modifiedAt, Integer completion) {

            this.postId = postId;
            this.category = category;
            this.title = title;
            this.content = content;
            this.completion = completion;
            this.createAt = createdAt;
            this.modifiedAt = modifiedAt;
        }

        public static ResponseDto<Object> toResponseDto(Response response) {
            return ResponseDto.success(response);
        }

        public static Response of(BkBoard bkBoard) {
            return builder()
                    .postId(bkBoard.getPostId())
                    .title(bkBoard.getTitle())
                    .content(bkBoard.getContent())
                    .completion(bkBoard.getCompletion())
                    .createdAt(bkBoard.getCreatedAt())
                    .modifiedAt(bkBoard.getModifiedAt())
                    .build();
        }
    }
}
