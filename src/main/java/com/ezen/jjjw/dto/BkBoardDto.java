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
        private Integer completion;
        private int share;

        public BkBoard toEntity(Member member) {
            return BkBoard.builder()
                    .member(member)
                    .postId(postId)
                    .category(category)
                    .title(title)
                    .content(content)
                    .completion(completion)
                    .share(share)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RequestCompletion {
        private Long postId;
        private Integer completion;
    }

    @Getter
    @NoArgsConstructor
    public static class RequestShare {
        private Long postId;
        private int share;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        //        private String memberId;
//        private Long postId; // 게시판 식별자
        private String category; // 카테고리
        private String title;  // 제목
        private String content;  // 내용
        private int completion;
        private int share;

        public BkBoard toEntity(Member member) {
            return BkBoard.builder()
                    .member(member)
//                    .postId(postId)
                    .category(category)
                    .title(title)
                    .content(content)
                    .completion(completion)
                    .share(share)
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
        private int completion; // 완료 여부
        private Integer existReview; // 리뷰 존재 유무
        private LocalDateTime createAt; // 생성일자
        private LocalDateTime modifiedAt; // 수정일자

        @Builder
        public Response(Long postId, String category, String title, String content, LocalDateTime createdAt, LocalDateTime modifiedAt, int completion, Integer existReview) {

            this.postId = postId;
            this.category = category;
            this.title = title;
            this.content = content;
            this.completion = completion;
            this.existReview = existReview;
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
                    .existReview(bkBoard.getExistReview())
                    .createdAt(bkBoard.getCreatedAt())
                    .modifiedAt(bkBoard.getModifiedAt())
                    .build();
        }
    }
}
