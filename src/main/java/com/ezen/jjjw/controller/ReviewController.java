package com.ezen.jjjw.controller;

import com.ezen.jjjw.dto.request.ReviewRequestDto;
import com.ezen.jjjw.dto.response.ReviewResponseDto;
import com.ezen.jjjw.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

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
    @PostMapping(value = "/create/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Integer> createReview(
            @PathVariable Long postId,
            @RequestPart("reviewContent") String reviewContent,
            @RequestPart(value = "fileUrl", required = false) List<MultipartFile> multipartFiles) throws IOException {
        return reviewService.createReview(postId, reviewContent, multipartFiles);
    }

    // 리뷰 게시글 상세 GET /review/detail/{postId}
    @GetMapping(value = "/detail/{postId}")
    public ResponseEntity<?> detailReview(@PathVariable Long postId) {
        return reviewService.findByPostId(postId);
    }

    // 리뷰 게시글 수정 PUT /review/update/{postId}
    @PutMapping(value = "/update/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateReview(@PathVariable Long postId,
                                          @ModelAttribute ReviewRequestDto reviewRequestDto,
                                          @RequestPart(value = "fileUrl", required = false) List<MultipartFile> multipartFiles
                                          ) {
       return reviewService.updateSave(postId, reviewRequestDto, multipartFiles);
    }

    // 리뷰 게시글 삭제 DELETE /review/delete/{postId}
    @DeleteMapping(value = "/delete/{postId}")
    public ResponseEntity<Integer> deleteReview(@PathVariable Long postId) {
        return reviewService.deleteByReviewId(postId);
    }
}