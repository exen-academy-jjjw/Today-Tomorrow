package com.ezen.jjjw.controller;

import com.ezen.jjjw.dto.request.ReviewRequestDto;
import com.ezen.jjjw.dto.response.ResponseDto;
import com.ezen.jjjw.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * packageName    : com.RSMboard.RSMboard.controller
 * fileName       : ReviewController.java
 * author         : won
 * date           : 2023-06-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-04        won       최초 생성
 */

@RequiredArgsConstructor
@RestController
@RequestMapping(value ="/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 게시글 작성 POST /review/create/{postId}
    @PostMapping(value = "/create/{postId}")
    public ResponseDto<?> createReview(@PathVariable Long postId,
                                       @RequestBody @Valid ReviewRequestDto reviewRequestDto, HttpServletRequest request) {
        System.out.println("정보: "+ reviewRequestDto);
        return reviewService.createReview(postId, reviewRequestDto, request);
    }

    // 리뷰 게시글 상세 GET /review/detail/{postId}
    @GetMapping(value = "/detail/{postId}")
    public ResponseDto<?> detailReview(@PathVariable Long postId, HttpServletRequest request) {
        return reviewService.findByPostId(postId, request);
    }

    // 리뷰 게시글 수정 PUT /review/update/{postId}
    @PutMapping(value = "/update/{postId}")
    public ResponseDto<?> updateReview(@PathVariable Long postId, @RequestBody @Valid ReviewRequestDto requestDto, HttpServletRequest request) {
        return reviewService.updateSave(postId, requestDto, request);
    }

    // 리뷰 게시글 삭제 DELETE /review/delete/{postId}
    @DeleteMapping(value = "/delete/{postId}")
    public ResponseDto<?> deleteReview(@PathVariable Long postId, HttpServletRequest request) {
        return reviewService.deleteByReviewId(postId, request);
    }
}