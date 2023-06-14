package com.ezen.jjjw.controller;

import com.ezen.jjjw.dto.request.ReviewRequestDto;
import com.ezen.jjjw.dto.response.ReviewResponseDto;
import com.ezen.jjjw.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long postId, @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        return reviewService.createReview(postId, reviewRequestDto);
    }

    // 리뷰 게시글 상세 GET /review/detail/{postId}
    @GetMapping(value = "/detail/{postId}")
    public ResponseEntity<ReviewResponseDto> detailReview(@PathVariable Long postId) {
        return reviewService.findByPostId(postId);
    }

    // 리뷰 게시글 수정 PUT /review/update/{postId}
    @PutMapping(value = "/update/{postId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long postId, @RequestBody @Valid ReviewRequestDto requestDto) {
        return reviewService.updateSave(postId, requestDto);
    }

    // 리뷰 게시글 삭제 DELETE /review/delete/{postId}
    @DeleteMapping(value = "/delete/{postId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long postId) {
        return reviewService.deleteByReviewId(postId);
    }
}