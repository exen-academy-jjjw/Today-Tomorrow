package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.dto.request.ReviewRequestDto;
import com.ezen.jjjw.dto.response.ReviewResponseDto;
import com.ezen.jjjw.exception.CustomExceptionHandler;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * packageName    : com.RSMboard.RSMboard.service
 * fileName       : ReviewService.java
 * author         : won
 * date           : 2023-06-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-04        won              최초 생성
 */


@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BkBoardRepository bkBoardRepository;

    private final ImageService imageService;
    private final CustomExceptionHandler customExceptionHandler;

    // 리뷰 게시글 작성 POST /review/create/{postId}
    @Transactional
    public ResponseEntity<Integer> createReview(Long postId, String reviewContent, List<MultipartFile> multipartFiles) throws IOException {
        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        int existReview = bkBoard.getExistReview();
        if (existReview == 1) {
            log.info("리뷰가 존재하는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        Review review = Review.builder()
                .reviewContent(reviewContent)
                .bkBoard(bkBoard)
                .build();
        bkBoard.updateExistReview(bkBoard);
        bkBoardRepository.save(bkBoard);
        reviewRepository.save(review);
        log.info("리뷰 작성 성공");

        // ImageService를 사용하여 이미지 파일 처리
        List<String> imageUrlList = imageService.uploadImages(multipartFiles, "bucket", review);

        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional(readOnly = true)
    public BkBoard isPresentPost(Long id) {
        Optional<BkBoard> optionalPost = bkBoardRepository.findById(id);
        return optionalPost.orElse(null);
    }


    // 리뷰 게시글 상세 GET /review/detail/{postId}
    @Transactional(readOnly = true)
    public ResponseEntity<?> findReviewByPostId(Long postId) {

        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        customExceptionHandler.getNotFoundReviewStatusOrgetReview(bkBoard);
        Review findReview = bkBoard.getReview();
        List<String> imageUrls = imageService.findImageUrlsByReviewId(findReview.getId());

        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                .id(findReview.getId())
                .postId(findReview.getBkBoard().getPostId())
                .fileUrlList(imageUrls)
                .reviewContent(findReview.getReviewContent())
                .createdAt(findReview.getCreatedAt())
                .modifiedAt(findReview.getModifiedAt())
                .build();

        return ResponseEntity.ok(reviewResponseDto);
    }

    // 리뷰 게시글 수정 PUT /review/update/{postId}
    @Transactional
    public ResponseEntity<?> updateSave(Long postId, ReviewRequestDto reviewRequestDto, List<MultipartFile> multipartFiles) throws IOException {

        // 게시글 유효성 검증
        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        // 리뷰 유효성 검증
        customExceptionHandler.getNotFoundReviewStatusOrgetReview(bkBoard);
        Review findReview = bkBoard.getReview();

        // reviewContent 업데이트
        findReview.update(reviewRequestDto.getReviewContent());
        reviewRepository.save(findReview.getBkBoard().getReview());
        log.info("리뷰 수정 성공");

        // ImageService를 사용하여 리뷰와 관련된 이미지 업데이트
        imageService.updateImagesForReview(findReview.getId(), multipartFiles, findReview);

        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    // 리뷰 게시글 삭제 DELETE /review/delete/{postId}
    @Transactional
    public ResponseEntity<Integer> deleteByReviewId(Long postId) {

        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        customExceptionHandler.getNotFoundReviewStatusOrgetReview(bkBoard);
        Review findReview = bkBoard.getReview();

        reviewRepository.delete(findReview);
        log.info("리뷰 삭제 성공");

        imageService.deleteImagesByReviewId(findReview.getId());

        bkBoard.deleteExistReview(bkBoard);
        bkBoardRepository.save(bkBoard);

        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }
}
